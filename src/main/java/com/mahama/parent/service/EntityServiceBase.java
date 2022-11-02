package com.mahama.parent.service;

import com.mahama.common.utils.Convert;
import com.mahama.common.utils.DateUtil;
import com.mahama.common.utils.StringUtil;
import com.mahama.common.utils.BeanUtil;
import com.mahama.corecommon.annotation.SpecField;
import com.mahama.parent.tools.MyQueryParams;
import com.mahama.parent.tools.MySpecification;
import com.mahama.parent.vo.PageData;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import javax.persistence.Transient;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EntityServiceBase {
    /**
     * @param model  数据源
     * @param entity 实体类
     * @param <M>
     * @param <T>
     * @return
     * @throws IllegalArgumentException
     */
    public <M, T> Example<T> getExample(M model, T entity) throws IllegalArgumentException {
        return getExample(model, entity, "");
    }

    /**
     * @param model          数据源
     * @param entity         实体类
     * @param containsColumn 模糊匹配字段，使用","间隔
     * @param <T>
     * @return
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    public <M, T> Example<T> getExample(M model, T entity, String containsColumn) throws IllegalArgumentException {
        try {
            //优先使用排除空值的方案
            BeanUtil.copyPropertiesWithOutNull(model, entity);
        } catch (Exception err) {
            BeanUtil.copyPropertiesWithOutNull(model, entity);
        }
        ExampleMatcher matcher = ExampleMatcher.matching();
        for (String str : Convert.toStrArray(",", containsColumn)) {
            matcher = matcher.withMatcher(str, ExampleMatcher.GenericPropertyMatchers.contains());
        }
        matcher.withIgnoreNullValues();
        return Example.of(entity, matcher);
    }

    public List<Sort.Order> getSort(String sortColumn) {
        return getSort(sortColumn, "desc");
    }

    public List<Sort.Order> getSort(String sortColumn, String order) {
        List<Sort.Order> sort = new ArrayList<>();
        for (String str : Convert.toStrArray(",", sortColumn)) {
            sort.add(new Sort.Order(Sort.Direction.fromString(order), str));
        }
        return sort;
    }

    public <M extends PageData> PageRequest getPage(M model) {
        try {
            Integer page = model.getPage();
            Integer limit = model.getLimit();
            String sort = model.getSort();
            String order = model.getOrder().name();
            if (StringUtil.isNullOrEmpty(sort)) {
                return PageRequest.of(page - 1, limit);
            } else if (StringUtil.isNotNullOrEmpty(order)) {
                return PageRequest.of(page - 1, limit, Sort.by(getSort(sort, order)));
            } else {
                return PageRequest.of(page - 1, limit, Sort.by(getSort(sort)));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public <M, T> MySpecification<T> getSpecification(M model) {
        return getSpecification(model, "");
    }

    public <M, T> MySpecification<T> getSpecification(M model, String containsColumn) {
        return getSpecification(model, containsColumn, "");
    }

    public <M, T> MySpecification<T> getSpecification(M model, String containsColumn, String thanColumn) {
        return getSpecification(model, containsColumn, thanColumn, "");
    }

    /**
     * 复杂查询
     *
     * @param model          model
     * @param containsColumn 模糊匹配字段
     * @param thanColumn     区间字段
     * @param <M>            M
     * @param <T>            T
     * @return
     */
    public <M, T> MySpecification<T> getSpecification(M model, String containsColumn, String thanColumn, String null2IsNullColumn) {
        MySpecification<T> spec = new MySpecification<>();
        try {
            List<String> containsList = Convert.toStrList(",", containsColumn);
            List<String> thanList = Convert.toStrList(",", thanColumn);
            List<String> null2IsNullList = Convert.toStrList(",", null2IsNullColumn);
            Field[] fields = model.getClass().getDeclaredFields();
            for (Field field : fields) {
                Transient trans = field.getAnnotation(Transient.class);
                SpecField specModel = field.getAnnotation(SpecField.class);
                if (trans != null) {
                    continue;
                }
                String name = field.getName();
                String getModelName = "get" + name.substring(0, 1).toUpperCase() + name.substring(1);
                Method m = model.getClass().getMethod(getModelName);
                if (containsList.contains(name)) {
                    if (field.getType().equals(String.class)) {
                        String value = (String) m.invoke(model);
                        if (StringUtil.isNotNullOrEmpty(value)) {
                            spec.and(MyQueryParams.like(name, value));
                        }
                    }
                } else if (thanList.contains(name)) {
                    if (field.getType().equals(List.class)) {
                        List<Class<?>> value = (List<Class<?>>) m.invoke(model, new Class<?>[0]);
                        if (value != null) {
                            if (value.size() > 0) {
                                if (specModel != null) {
                                    if (Date.class.equals(specModel.listItemClass())) {
                                        Date start = DateUtil.parse(String.valueOf(value.get(0)), specModel.datePattern());
                                        spec.and(MyQueryParams.ge(name, start));
                                    } else {
                                        spec.and(MyQueryParams.ge(name, value.get(0)));
                                    }
                                } else {
                                    Date start = DateUtil.parseTime(String.valueOf(value.get(0)));
                                    if (start != null) {
                                        spec.and(MyQueryParams.le(name, start));
                                    } else {
                                        spec.and(MyQueryParams.le(name, value.get(0)));
                                    }
                                }
                            }
                            if (value.size() > 1) {
                                if (specModel != null) {
                                    if (Date.class.equals(specModel.listItemClass())) {
                                        Date start = DateUtil.parse(String.valueOf(value.get(1)), specModel.datePattern());
                                        spec.and(MyQueryParams.ge(name, start));
                                    } else {
                                        spec.and(MyQueryParams.ge(name, value.get(1)));
                                    }
                                } else {
                                    Date start = DateUtil.parseTime(String.valueOf(value.get(1)));
                                    if (start != null) {
                                        spec.and(MyQueryParams.le(name, start));
                                    } else {
                                        spec.and(MyQueryParams.le(name, value.get(1)));
                                    }
                                }
                            }
                        }
                    }
                } else {
                    if (!field.getType().equals(List.class)) {
                        Object value = m.invoke(model, new Object[0]);
                        if (StringUtil.isNotNullOrEmpty(value)) {
                            spec.and(MyQueryParams.eq(name, value));
                        } else if (null2IsNullList.contains(name)) {
                            spec.and(MyQueryParams.isNull(name));
                        }
                    } else {
                        List<Class<?>> value = (List<Class<?>>) m.invoke(model, new Class<?>[0]);
                        spec.and(MyQueryParams.in(name, value));
                    }
                }
            }
        } catch (Exception ignored) {
        }
        return spec;
    }
}
