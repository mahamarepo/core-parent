package com.mahama.parent.service;

import com.mahama.common.exception.AssertionException;
import com.mahama.common.utils.Assert;
import com.mahama.common.utils.ReflectionUtil;
import com.mahama.common.utils.RedisHelp;
import com.mahama.parent.config.RedisConfig;
import com.mahama.parent.vo.ListData;
import com.mahama.parent.vo.PageData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.TimeUnit;

public abstract class EntityService<JPA extends JpaRepository<T, ID>, T extends Serializable, ID> extends EntityServiceBase {
    public interface Callback<E> {
        void deleteOtherKey(E data);
    }

    private Class<T> currentModelClass() {
        return ReflectionUtil.getSuperClassGenericType(this.getClass(), 1);
    }

    protected Class<T> entityClass = this.currentModelClass();
    public Callback<T> callback = null;
    @Autowired
    private JPA jpaRepository;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private RedisConfig redisConfig;

    public JPA getJpaRepository() {
        return jpaRepository;
    }

    protected T newT() {
        try {
            return (T) entityClass.getDeclaredConstructor().newInstance();
        } catch (Exception err) {
            throw new AssertionException("查询条件转换失败，请联系管理员");
        }
    }

    /**
     * 根据id查询对应的实体。
     *
     * @param id 主键值
     * @return 实体类
     */
    public T findById(ID id) {
        return findById(id, 1, TimeUnit.HOURS);
    }

    public T findById(ID id, long timeout, TimeUnit unit) {
        if (id == null) {
            return null;
        }
        return getRedisHelp().queryById(id,
                () -> getJpaRepository().findById(id).orElse(null),
                timeout, unit);
    }

    /**
     * 根据id查询对应的实体是否存在。
     */
    public boolean existsById(ID id) {
        return getJpaRepository().existsById(id);
    }

    /**
     * 根据条件查询对应的实体是否存在。
     */
    public <M> boolean exists(M model) {
        return getJpaRepository().exists(getExample(model, newT()));
    }

    /**
     * 根据条件查询对应的实体是否存在并设置缓存
     */
    public <M> boolean exists(M model, long timeout, TimeUnit unit) {
        return getRedisHelp().query("list_exists_" + model.hashCode(),
                () -> getJpaRepository().exists(getExample(model, newT())),
                timeout,
                unit);
    }

    /**
     * 新增数据
     *
     * @param entity 不带主键值实体类
     * @return 执行的记录
     */
    public <S extends T> S save(S entity) {
        S result = getRedisHelp().save(() -> getJpaRepository().save(entity));
        if (callback != null)
            callback.deleteOtherKey(result);
        return result;
    }

    /**
     * 修改数据
     *
     * @param id     主键ID的值(设置缓存)
     * @param entity 带主键值实体类
     * @return 执行的记录
     */
    public <S extends T> S update(ID id, S entity) {
        S result = getRedisHelp().updateById(id,
                () -> getJpaRepository().save(entity));
        if (callback != null)
            callback.deleteOtherKey(result);
        return result;
    }

    public <S extends T> void saveOrUpdateAll(Iterable<S> entities) {
        Iterable<S> result = getRedisHelp().execute(null,
                () -> getJpaRepository().saveAll(entities));
        if (callback != null)
            for (S item : result) {
                callback.deleteOtherKey(item);
            }
    }

    public List<T> findAll() {
        return findAll(1, TimeUnit.HOURS);
    }

    public List<T> findAll(long timeout, TimeUnit unit) {
        return getRedisHelp().query("list_all",
                getJpaRepository()::findAll,
                timeout, unit);
    }

    public List<T> findAll(Sort sort) {
        return findAll(sort, 1, TimeUnit.HOURS);
    }

    public List<T> findAll(Sort sort, long timeout, TimeUnit unit) {
        return getRedisHelp().query("list_sort_" + sort.hashCode(),
                () -> getJpaRepository().findAll(sort),
                timeout, unit);
    }

    public List<T> findAll(Example<T> example) {
        return findAll(example, 1, TimeUnit.HOURS);
    }

    public List<T> findAll(Example<T> example, long timeout, TimeUnit unit) {
        return getRedisHelp().query("list_" + example.hashCode(),
                () -> getJpaRepository().findAll(example),
                timeout, unit);
    }

    public List<T> findAll(Example<T> example, Sort sort) {
        return findAll(example, sort, 1, TimeUnit.HOURS);
    }

    public List<T> findAll(Example<T> example, Sort sort, long timeout, TimeUnit unit) {
        return getRedisHelp().query("list_" + example.hashCode() + "_sort_" + sort.hashCode(),
                () -> getJpaRepository().findAll(example, sort),
                timeout, unit);
    }

    public Page<T> findAll(PageRequest page) {
        return findAll(page, 1, TimeUnit.HOURS);
    }

    public Page<T> findAll(PageRequest page, long timeout, TimeUnit unit) {
        return getRedisHelp().query("list_page_" + page.hashCode(),
                () -> getJpaRepository().findAll(page),
                timeout, unit);
    }

    public <M> Page<T> findAll(M model, PageData pageData) {
        return findAll(model, pageData, "");
    }

    public <M> Page<T> findAll(M model, PageData pageData, String containsColumn) {
        return findAll(model, pageData, containsColumn, 1, TimeUnit.HOURS);
    }

    public <M> Page<T> findAll(M model, PageData pageData, long timeout, TimeUnit unit) {
        return findAll(getExample(model, newT()), getPage(pageData), timeout, unit);
    }

    public <M> Page<T> findAll(M model, PageData pageData, String containsColumn, long timeout, TimeUnit unit) {
        return findAll(getExample(model, newT(), containsColumn), getPage(pageData), timeout, unit);
    }

    public Page<T> findAll(Example<T> example, PageRequest page) {
        return findAll(example, page, 1, TimeUnit.HOURS);
    }

    public Page<T> findAll(Example<T> example, PageRequest page, long timeout, TimeUnit unit) {
        return getRedisHelp().query("list_" + example.hashCode() + "_page_" + page.hashCode(),
                () -> getJpaRepository().findAll(example, page),
                timeout, unit);
    }

    public <M> List<T> findAll(M model) {
        return findAll(model, "");
    }

    public <M> List<T> findAll(M model, String containsColumn) {
        return findAll(model, containsColumn, 1, TimeUnit.HOURS);
    }

    public <M> List<T> findAll(M model, String containsColumn, long timeout, TimeUnit unit) {
        return findAll(getExample(model, newT(), containsColumn), timeout, unit);
    }

    public <M extends ListData> List<T> findAll(M model) {
        return findAll(model, "");
    }

    public <M extends ListData> List<T> findAll(M model, String containsColumn) {
        return findAll(model, containsColumn, 1, TimeUnit.HOURS);
    }

    public <M extends ListData> List<T> findAll(M model, String containsColumn, long timeout, TimeUnit unit) {
        return findAll(getExample(model, newT(), containsColumn),
                Sort.by(getSort(model.getSort(), model.getOrder().name())),
                timeout, unit);
    }

    public <M extends PageData> Page<T> findAll(M model) {
        return findAll(model, "");
    }

    public <M extends PageData> Page<T> findAll(M model, String containsColumn) {
        return findAll(model, containsColumn, 1, TimeUnit.HOURS);
    }

    public <M extends PageData> Page<T> findAll(M model, String containsColumn, long timeout, TimeUnit unit) {
        return findAll(getExample(model, newT(), containsColumn), getPage(model), timeout, unit);
    }

    public <M> long count(M model) {
        return count(model, 1, TimeUnit.HOURS);
    }

    public <M> long count(M model, long timeout, TimeUnit unit) {
        return getRedisHelp().query("list_count_" + model.hashCode(), () -> getJpaRepository().count(getExample(model, newT())), timeout, unit);
    }


    public <M extends ListData> List<T> findAllBySpec(M model) {
        return findAllBySpec(model, "");
    }

    public <M extends ListData> List<T> findAllBySpec(M model, String containsColumn) {
        return findAllBySpec(model, containsColumn, "");
    }

    public <M extends ListData> List<T> findAllBySpec(M model, String containsColumn, String thanColumn) {
        return findAllBySpec(model, containsColumn, thanColumn, 1, TimeUnit.HOURS);
    }

    public <M extends ListData> List<T> findAllBySpec(M model, String containsColumn, String thanColumn, String null2IsNull) {
        return findAllBySpec(model, containsColumn, thanColumn, null2IsNull, 1, TimeUnit.HOURS);
    }

    public <M extends ListData> List<T> findAllBySpec(M model, String containsColumn, String thanColumn, long timeout, TimeUnit unit) {
        return findAllBySpec(model, containsColumn, thanColumn, "", timeout, unit);
    }

    public <M extends ListData> List<T> findAllBySpec(M model, String containsColumn, String thanColumn, String null2IsNull, long timeout, TimeUnit unit) {
        JpaSpecificationExecutor<T> jpaSpecificationExecutor = (JpaSpecificationExecutor<T>) getJpaRepository();
        return getRedisHelp().query("list_" + model.hashCode(),
                () -> jpaSpecificationExecutor.findAll(getSpecification(model, containsColumn, thanColumn, null2IsNull)),
                timeout,
                unit
        );
    }


    public <M extends PageData> Page<T> findAllBySpec(M model) {
        return findAllBySpec(model, "");
    }

    public <M extends PageData> Page<T> findAllBySpec(M model, String containsColumn) {
        return findAllBySpec(model, containsColumn, "");
    }

    public <M extends PageData> Page<T> findAllBySpec(M model, String containsColumn, String thanColumn) {
        return findAllBySpec(model, containsColumn, thanColumn, "");
    }

    public <M extends PageData> Page<T> findAllBySpec(M model, String containsColumn, String thanColumn, String null2IsNull) {
        return findAllBySpec(model, containsColumn, thanColumn, null2IsNull, 1, TimeUnit.HOURS);
    }

    public <M extends PageData> Page<T> findAllBySpec(M model, String containsColumn, String thanColumn, long timeout, TimeUnit unit) {
        return findAllBySpec(model, containsColumn, thanColumn, "", timeout, unit);
    }

    public <M extends PageData> Page<T> findAllBySpec(M model, String containsColumn, String thanColumn, String null2IsNull, long timeout, TimeUnit unit) {
        JpaSpecificationExecutor<T> jpaSpecificationExecutor = (JpaSpecificationExecutor<T>) getJpaRepository();
        return getRedisHelp().query("list_page_" + model.hashCode(),
                () -> jpaSpecificationExecutor.findAll(getSpecification(model, containsColumn, thanColumn, null2IsNull), getPage(model)),
                timeout,
                unit
        );
    }



    public List<T> findAllBySpec(Specification<T> spec) {
        return findAllBySpec(spec, 1, TimeUnit.HOURS);
    }

    public List<T> findAllBySpec(Specification<T> spec, long timeout, TimeUnit unit) {
        JpaSpecificationExecutor<T> jpaSpecificationExecutor = (JpaSpecificationExecutor<T>) getJpaRepository();
        return getRedisHelp().query("list_page_" + spec.hashCode(),
                () -> jpaSpecificationExecutor.findAll(spec),
                timeout,
                unit
        );
    }

    public <M extends PageData> Page<T> findAllBySpec(Specification<T> spec, M page) {
        return findAllBySpec(spec, page, 1, TimeUnit.HOURS);
    }

    public <M extends PageData> Page<T> findAllBySpec(Specification<T> spec, M page, long timeout, TimeUnit unit) {
        JpaSpecificationExecutor<T> jpaSpecificationExecutor = (JpaSpecificationExecutor<T>) getJpaRepository();
        return getRedisHelp().query("list_page_" + spec.hashCode(),
                () -> jpaSpecificationExecutor.findAll(spec, getPage(page)),
                timeout,
                unit
        );
    }

    public long countBySpec(Specification<T> spec) {
        return countBySpec(spec, 1, TimeUnit.HOURS);
    }

    public long countBySpec(Specification<T> spec, long timeout, TimeUnit unit) {
        JpaSpecificationExecutor<T> jpaSpecificationExecutor = (JpaSpecificationExecutor<T>) getJpaRepository();
        return getRedisHelp().query("list_count_" + spec.hashCode(),
                () -> jpaSpecificationExecutor.count(spec),
                timeout,
                unit
        );
    }

    public T findBySpec(Specification<T> spec) {
        return findBySpec(spec, 1, TimeUnit.HOURS);
    }

    public T findBySpec(Specification<T> spec, long timeout, TimeUnit unit) {
        JpaSpecificationExecutor<T> jpaSpecificationExecutor = (JpaSpecificationExecutor<T>) getJpaRepository();
        return getRedisHelp().query("spec_" + spec.hashCode(),
                () -> jpaSpecificationExecutor.findOne(spec).orElse(null),
                timeout,
                unit
        );
    }


    /**
     * 根据id删除对应的实体。
     */
    public void deleteById(ID id) {
        getRedisHelp().delById(id, () -> {
            T entity = getJpaRepository().findById(id).orElse(null);
            Assert.notNull(entity, "");
            getJpaRepository().deleteById(id);
            if (callback != null)
                callback.deleteOtherKey(entity);
        });
    }

    /**
     * 删除给定的实体。
     */
    public void delete(T entity) {
        getRedisHelp().del(() -> getJpaRepository().delete(entity));
        if (callback != null)
            callback.deleteOtherKey(entity);
    }

    /**
     * 删除给定的实体。
     */
    public <S extends T> void deleteAll(Iterable<S> entities) {
        getRedisHelp().del(() -> getJpaRepository().deleteAll(entities));
        if (callback != null)
            for (S item : entities) {
                callback.deleteOtherKey(item);
            }
    }

    public abstract String redisKeyPrefix();

    public abstract String redisKeyGroup();

    public boolean redisDisabled() {
        return redisConfig.isDisabled();
    }

    public String getKeyPrefix() {
        return redisKeyPrefix() + "_" + redisKeyGroup();
    }

    public RedisHelp getRedisHelp() {
        return new RedisHelp(redisTemplate, getKeyPrefix()).disabled(redisDisabled());
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
