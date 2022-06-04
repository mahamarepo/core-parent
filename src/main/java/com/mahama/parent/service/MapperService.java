package com.mahama.parent.service;

import com.baomidou.mybatisplus.core.conditions.AbstractWrapper;
import com.baomidou.mybatisplus.core.conditions.ISqlSegment;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.interfaces.Func;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.*;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.mahama.common.exception.ApplicationWithoutLoggerException;
import com.mahama.common.utils.Lists;
import com.mahama.common.utils.ReflectionUtil;
import com.mahama.common.utils.StringUtil;
import com.mahama.parent.utils.BeanUtil;
import com.mahama.parent.utils.RedisHelp;
import com.mahama.parent.vo.ListData;
import com.mahama.parent.vo.Order;
import com.mahama.parent.vo.PageData;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Function;

public abstract class MapperService<M extends BaseMapper<T>, T> implements IService<T> {
    public interface Callback<E> {
        /**
         * 修改成功后清除其他缓存的回调方法
         *
         * @param data 修改成功后的实体类
         */
        void deleteOtherKey(E data);
    }

    Log log = LogFactory.getLog(this.getClass());
    public Callback<T> callback = null;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private M mapper;

    protected Class<T> entityClass = this.currentModelClass();

    @Override
    public M getBaseMapper() {
        return mapper;
    }

    @Override
    public Class<T> getEntityClass() {
        return entityClass;
    }

    private Class<T> currentModelClass() {
        return ReflectionUtil.getSuperClassGenericType(this.getClass(), 1);
    }

    private String getSqlStatement(SqlMethod sqlMethod) {
        return SqlHelper.getSqlStatement(mapper.getClass(), sqlMethod);
    }

    private <E> boolean executeBatch(Collection<E> list, int batchSize, BiConsumer<SqlSession, E> consumer) {
        return SqlHelper.executeBatch(this.entityClass, this.log, list, batchSize, consumer);
    }

    private <E> boolean executeBatch(Collection<E> list, BiConsumer<SqlSession, E> consumer) {
        return executeBatch(list, 1000, consumer);
    }

    public <P extends PageData> IPage<T> getPage(P pageData) {
        return new Page<>(pageData.getPage(), pageData.getLimit());
    }

    private String getKeyProperty() {
        TableInfo tableInfo = TableInfoHelper.getTableInfo(entityClass);
        Assert.notNull(tableInfo, "error: 没有找到表对应的实体类", new Object[0]);
        String keyProperty = tableInfo.getKeyProperty();
        Assert.notEmpty(keyProperty, "error: 表未设置主键", new Object[0]);
        return keyProperty;
    }

    public Object getIdVal(T entity) {
        return getIdVal(entity, getKeyProperty());
    }

    public Object getIdVal(T entity, String keyProperty) {
        return ReflectionKit.getFieldValue(entity, keyProperty);
    }

    public T newT() {
        try {
            return entityClass.getDeclaredConstructor().newInstance();
        } catch (Exception err) {
            throw new ApplicationWithoutLoggerException("查询条件转换失败，请联系管理员");
        }
    }

    /**
     * 根据 实体类 查询；有多个数据时抛出异常
     */
    public T getOne(T entity) {
        return getOne(Wrappers.lambdaQuery(entity));
    }

    /**
     * 根据 实体类 查询
     */
    public T getOne(T entity, boolean throwEx) {
        return getOne(Wrappers.lambdaQuery(entity), throwEx);
    }

    public List<T> list(T entity) {
        return list(Wrappers.query(entity));
    }

    public <S extends ListData> List<T> list(S model) {
        T entity = newT();
        BeanUtil.copyProperties(model, entity);
        return list(Wrappers.query(entity));
    }


    public interface PageBefore<T> {
        void run(LambdaQueryWrapper<T> wrapper);
    }

    public <P extends PageData> IPage<T> page(P model) {
        return page(model, null);
    }

    public <P extends PageData> IPage<T> page(P model, PageBefore<T> before) {
        T entity = newT();
        BeanUtil.copyPropertiesWithOutNull(model, entity);
        var wrapper = Wrappers.query(entity);
        if (StringUtil.isNotNullOrEmpty(model.getSort())) {
            String[] py = model.getSort().split(",");
            List<String> list = new ArrayList<>();
            for (String p : py) {
                list.add(StringUtil.camelToUnderline(p));
            }
            wrapper.orderBy(true, Order.desc.equals(model.getOrder()), list);
        } else {
            wrapper.orderByDesc(getKeyProperty());
        }
        if (before != null) {
            before.run(wrapper.lambda());
        }
        return page(getPage(model), wrapper);
    }

    public <E extends IPage<T>> E page(E page, LambdaQueryWrapper<T> queryWrapper, SFunction<T, ?> orderBy) {
        queryWrapper.orderByDesc(orderBy);
        return page(page, queryWrapper);
    }

    /**
     * 根据 ID 查询
     */
    @Override
    public T getById(Serializable id) {
        return getRedisHelp().queryById(id, () -> mapper.selectById(id));
    }

    public T getById(Serializable id, long timeout, TimeUnit unit) {
        return getRedisHelp().queryById(id, () -> mapper.selectById(id), timeout, unit);
    }

    @Override
    public T getOne(Wrapper<T> queryWrapper, boolean throwEx) {
        return throwEx ? mapper.selectOne(queryWrapper) : SqlHelper.getObject(log, mapper.selectList(queryWrapper));
    }

    @Override
    public boolean save(T entity) {
        boolean result = getRedisHelp().save(() -> SqlHelper.retBool(mapper.insert(entity)));
        if (result && callback != null)
            callback.deleteOtherKey(entity);
        return result;
    }

    @Override
    public boolean updateById(T entity) {
        Object idVal = getIdVal(entity);
        boolean result = getRedisHelp().updateById(idVal, () -> mapper.updateById(entity) > 0);
        if (result && callback != null)
            callback.deleteOtherKey(entity);
        return result;
    }

    public boolean updateById(Serializable id, T entity) {
        boolean result = getRedisHelp().updateById(id, () -> mapper.updateById(entity) > 0);
        if (result && callback != null)
            callback.deleteOtherKey(entity);
        return result;
    }

    public boolean updateById(Serializable id, Wrapper<T> updateWrapper) {
        return update("id_" + id, null, updateWrapper);
    }

    @Override
    public boolean update(Wrapper<T> updateWrapper) {
        return update(null, updateWrapper);
    }

    @Override
    public boolean update(T entity, Wrapper<T> updateWrapper) {
        String keyAppend = null;
        if (entity != null) {
            Object idVal = getIdVal(entity);
            if (idVal != null) {
                keyAppend = "id_" + getIdVal(entity);
            }
        }
        return update(keyAppend, entity, updateWrapper);
    }

    public boolean update(String keyAppend, T entity, Wrapper<T> updateWrapper) {
        return getRedisHelp().execute(keyAppend, () -> mapper.update(entity, updateWrapper) > 0);
    }

    @Override
    public boolean removeById(Serializable id) {
        return getRedisHelp().delById(id, () -> mapper.deleteById(id) > 0);
    }

    @Override
    public boolean remove(Wrapper<T> queryWrapper) {
        return getRedisHelp().del(() -> mapper.delete(queryWrapper) > 0);
    }

    @Override
    public boolean removeByIds(Collection<? extends Serializable> idList) {
        var result = mapper.deleteBatchIds(idList) > 0;
        if (result)
            idList.forEach(id -> getRedisHelp().deleteIdCacheKey(id));
        return result;
    }

    /**
     * 插入（批量）
     */
    @Override
    public boolean saveBatch(Collection<T> entityList, int batchSize) {
        var mapperService = this;
        return getRedisHelp().save(() -> {
            String sqlStatement = mapperService.getSqlStatement(SqlMethod.INSERT_ONE);
            return executeBatch(entityList, batchSize, (sqlSession, entity) -> {
                sqlSession.insert(sqlStatement, entity);
            });
        });
    }

    /**
     * 批量修改插入
     */
    @Override
    public boolean saveOrUpdateBatch(Collection<T> entityList, int batchSize) {
        String keyProperty = getKeyProperty();
        var result = SqlHelper.saveOrUpdateBatch(entityClass, mapper.getClass(), log, entityList, batchSize, (sqlSession, entity) -> {
            Object idVal = getIdVal(entity, keyProperty);
            return StringUtils.checkValNull(idVal) || CollectionUtils.isEmpty(sqlSession.selectList(getSqlStatement(SqlMethod.SELECT_BY_ID), entity));
        }, (sqlSession, entity) -> {
            MapperMethod.ParamMap<T> param = new MapperMethod.ParamMap<>();
            param.put("et", entity);
            sqlSession.update(getSqlStatement(SqlMethod.UPDATE_BY_ID), param);
            var idVal = getIdVal(entity);
            getRedisHelp().deleteIdCacheKey(idVal);
        });
        if (result) {
            getRedisHelp().clearCacheList();
        }
        return result;
    }

    /**
     * 根据ID 批量更新
     */
    @Override
    public boolean updateBatchById(Collection<T> entityList, int batchSize) {
        String sqlStatement = getSqlStatement(SqlMethod.UPDATE_BY_ID);
        var result = executeBatch(entityList, batchSize, (sqlSession, entity) -> {
            MapperMethod.ParamMap<T> param = new MapperMethod.ParamMap<>();
            param.put("et", entity);
            sqlSession.update(sqlStatement, param);
            var idVal = getIdVal(entity);
            getRedisHelp().deleteIdCacheKey(idVal);
        });
        if (result) {
            getRedisHelp().clearCacheList();
        }
        return result;
    }

    /**
     * TableId 注解存在更新记录，否插入一条记录
     */
    @Override
    public boolean saveOrUpdate(T entity) {
        if (null == entity) {
            return false;
        } else {
            Serializable idVal = (Serializable) getIdVal(entity);
            if (StringUtil.isNotNullOrEmpty(idVal) && !Objects.isNull(getById(idVal))) {
                return updateById(idVal, entity);
            } else {
                return save(entity);
            }
        }
    }

    /**
     * 根据 Wrapper，查询一条记录
     */
    @Override
    public Map<String, Object> getMap(Wrapper<T> queryWrapper) {
        return SqlHelper.getObject(log, mapper.selectMaps(queryWrapper));
    }

    /**
     * 根据 Wrapper，查询一条记录
     */
    @Override
    public <V> V getObj(Wrapper<T> queryWrapper, Function<? super Object, V> mapper) {
        return SqlHelper.getObject(log, listObjs(queryWrapper, mapper));
    }

    public abstract String redisKeyPrefix();

    public abstract String redisKeyGroup();

    public String getKeyPrefix() {
        return redisKeyPrefix() + "_" + redisKeyGroup();
    }

    public RedisHelp getRedisHelp() {
        return new RedisHelp(redisTemplate, getKeyPrefix());
    }

    public void clearAllRedis() {
        clearAllRedis("*");
    }

    public void clearAllRedis(String pattern) {
        new RedisHelp(redisTemplate, redisKeyPrefix()).clearCache(pattern);
    }

    public void clearGroupRedisByList(Long tenantId) {
        clearGroupRedisByList(tenantId + "");
    }

    public void clearGroupRedisByList(String group) {
        new RedisHelp(redisTemplate, redisKeyPrefix() + "_" + group).clearCache("list_*");
    }

    public void clearGroupRedis() {
        clearGroupRedis("*");
    }

    public void clearGroupRedis(String pattern) {
        getRedisHelp().clearCache(pattern);
    }

    public String getRedisIdKey() {
        return RedisHelp.getIdKey();
    }
}
