package com.mahama.parent.tools;

import com.mahama.common.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;

import javax.persistence.criteria.*;
import java.util.*;

/**
 * @author mahama
 * 使用示例
 * PageRequest pr = new PageRequest(0, 10);
 * Page pageData = userDao.findAll(new MySpecification<User>().and(
 * MyQueryParams.like("id", id),
 * MyQueryParams.like("Name",Name)).asc("id"), pr);
 */
public class MySpecification<T> implements Specification<T> {
    private final List<MyQuery> queryList = new ArrayList<>();

    //属性分隔
    private static final String PROPERTY_SEPARATOR = ".";

    //排序
    List<MyOrder> orders = new ArrayList<>();

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
        Predicate restrictions = cb.and(getPredicates(root, cb));
        cq.orderBy(getOrders(root, cb));
        return restrictions;
    }

    public Specification<T> toSpecification() {
        return this;
    }

    public MySpecification<T> and(Collection<MyQueryParams<?>> conditions) {
        MyQuery query = new MyQuery();
        query.setType(MyQueryType.AND);
        query.setList(conditions);
        queryList.add(query);
        return this;
    }

    public MySpecification<T> and(MyQueryParams<?>... conditions) {
        MyQuery query = new MyQuery();
        query.setType(MyQueryType.AND);
        query.setList(Arrays.asList(conditions));
        queryList.add(query);
        return this;
    }

    public MySpecification<T> or(Collection<MyQueryParams<?>> conditions) {
        MyQuery query = new MyQuery();
        query.setType(MyQueryType.OR);
        query.setList(conditions);
        queryList.add(query);
        return this;
    }

    public MySpecification<T> or(MyQueryParams<?>... conditions) {
        MyQuery query = new MyQuery();
        query.setType(MyQueryType.OR);
        query.setList(Arrays.asList(conditions));
        queryList.add(query);
        return this;
    }

    public MySpecification<T> desc(String property) {
        this.orders.add(MyOrder.desc(property));
        return this;
    }

    public <V> MySpecification<T> desc(MyFunction<T, V> func) {
        this.orders.add(MyOrder.desc(func.getFieldName()));
        return this;
    }

    public MySpecification<T> asc(String property) {
        this.orders.add(MyOrder.asc(property));
        return this;
    }

    public <V> MySpecification<T> asc(MyFunction<T, V> func) {
        this.orders.add(MyOrder.asc(func.getFieldName()));
        return this;
    }


    public MySpecification<T> order(String property, Sort.Direction direction) {
        this.orders.add(new MyOrder(property, direction));
        return this;
    }

    public MySpecification<T> orders(MyOrder... orders) {
        this.orders.addAll(Arrays.asList(orders));
        return this;
    }

    public MySpecification<T> orders(Collection<MyOrder> orders) {
        this.orders.addAll(orders);
        return this;
    }

    private Predicate[] andPredicates(Root<T> root, CriteriaBuilder cb, Collection<MyQueryParams<?>> list) {
        List<Predicate> listAnd = new ArrayList<>();
        for (MyQueryParams<?> condition : list) {
            if (condition == null) {
                continue;
            }

            Path<?> path = this.getPath(root, condition.property);
            if (path == null) {
                continue;
            }

            if (condition.query != null) {
                listAnd.add(getPredicates(root, cb, Lists.newArrayList(condition.query)));
                continue;
            }
            switch (condition.operator) {
                case eq:
                    if (condition.value != null) {
                        if (String.class.isAssignableFrom(path.getJavaType()) && condition.value instanceof String) {
                            if (!((String) condition.value).isEmpty()) {
                                listAnd.add(cb.equal(path, condition.value));
                            }
                        } else {
                            listAnd.add(cb.equal(path, condition.value));
                        }
                    }
                    break;
                case ge:
                    if (Number.class.isAssignableFrom(path.getJavaType()) && condition.value instanceof Number) {
                        listAnd.add(cb.ge((Path<Number>) path, (Number) condition.value));
                    }
                    if (String.class.isAssignableFrom(path.getJavaType()) && condition.value instanceof String) {
                        listAnd.add(cb.greaterThanOrEqualTo((Path<String>) path, (String) condition.value));
                    }
                    if (Date.class.isAssignableFrom(path.getJavaType()) && condition.value instanceof Date) {
                        listAnd.add(cb.greaterThanOrEqualTo((Path<Date>) path, (Date) condition.value));
                    }
                    break;
                case gt:
                    if (Number.class.isAssignableFrom(path.getJavaType()) && condition.value instanceof Number) {
                        listAnd.add(cb.gt((Path<Number>) path, (Number) condition.value));
                    }
                    if (String.class.isAssignableFrom(path.getJavaType()) && condition.value instanceof String) {
                        listAnd.add(cb.greaterThan((Path<String>) path, (String) condition.value));
                    }
                    if (Date.class.isAssignableFrom(path.getJavaType()) && condition.value instanceof Date) {
                        listAnd.add(cb.greaterThan((Path<Date>) path, (Date) condition.value));
                    }
                    break;
                case in:
                    listAnd.add(path.in(((Collection<?>)condition.value).toArray()));
                    break;
                case le:
                    if (Number.class.isAssignableFrom(path.getJavaType()) && condition.value instanceof Number) {
                        listAnd.add(cb.le((Path<Number>) path, (Number) condition.value));
                    }
                    if (String.class.isAssignableFrom(path.getJavaType()) && condition.value instanceof String) {
                        listAnd.add(cb.lessThanOrEqualTo((Path<String>) path, (String) condition.value));
                    }
                    if (Date.class.isAssignableFrom(path.getJavaType()) && condition.value instanceof Date) {
                        listAnd.add(cb.lessThanOrEqualTo((Path<Date>) path, (Date) condition.value));
                    }
                    break;
                case lt:
                    if (Number.class.isAssignableFrom(path.getJavaType()) && condition.value instanceof Number) {
                        listAnd.add(cb.lt((Path<Number>) path, (Number) condition.value));
                    }
                    if (String.class.isAssignableFrom(path.getJavaType()) && condition.value instanceof String) {
                        listAnd.add(cb.lessThan((Path<String>) path, (String) condition.value));
                    }
                    if (Date.class.isAssignableFrom(path.getJavaType()) && condition.value instanceof Date) {
                        listAnd.add(cb.lessThan((Path<Date>) path, (Date) condition.value));
                    }
                    break;
                case ne:
                    if (condition.value != null) {
                        listAnd.add(cb.notEqual(path, condition.value));
                    }
                    break;
                case like:
                    if (condition.value != null) {
                        if (String.class.isAssignableFrom(path.getJavaType()) && condition.value instanceof String && !((String) condition.value).isEmpty()) {
                            listAnd.add(cb.like((Path<String>) path, "%" + condition.value + "%"));
                        }
                    }
                    break;
                case iLike:
                    if (condition.value != null) {
                        if (String.class.isAssignableFrom(path.getJavaType()) && condition.value instanceof String && !((String) condition.value).isEmpty()) {
                            listAnd.add(cb.like((Path<String>) path, (String) condition.value));
                        }
                    }
                    break;
                case lLike:
                    if (condition.value != null) {
                        if (String.class.isAssignableFrom(path.getJavaType()) && condition.value instanceof String && !((String) condition.value).isEmpty()) {
                            listAnd.add(cb.like((Path<String>) path, "%" + condition.value));
                        }
                    }
                    break;
                case rLike:
                    if (condition.value != null) {
                        if (String.class.isAssignableFrom(path.getJavaType()) && condition.value instanceof String && !((String) condition.value).isEmpty()) {
                            listAnd.add(cb.like((Path<String>) path, condition.value + "%"));
                        }
                    }
                    break;
                case notIn:
                    listAnd.add(path.in(((Collection<?>)condition.value).toArray()).not());
                    break;
                case isNull:
                    listAnd.add(path.isNull());
                    break;
                case isNotNull:
                    listAnd.add(path.isNotNull());
                    break;
                default:
                    break;
            }
        }
        return listAnd.toArray(new Predicate[]{});
    }

    private Predicate getPredicates(Root<T> root, CriteriaBuilder cb) {
        return getPredicates(root, cb, queryList);
    }

    private Predicate getPredicates(Root<T> root, CriteriaBuilder cb, List<MyQuery> queries) {
        Predicate restrictions = cb.conjunction();
        for (MyQuery query : queries) {
            switch (query.type) {
                case AND:
                    restrictions = cb.and(restrictions, cb.and(andPredicates(root, cb, query.getList())));
                    break;
                case OR:
                    if (query.getList().size() > 1) {
                        restrictions = cb.and(restrictions, cb.or(andPredicates(root, cb, query.getList())));
                    } else if (query.getList().size() > 0) {
                        restrictions = cb.or(restrictions, andPredicates(root, cb, query.getList())[0]);
                    }
                    break;
                default:
                    break;
            }
        }
        return restrictions;
    }

    private List<javax.persistence.criteria.Order> getOrders(Root<T> root, CriteriaBuilder cb) {
        List<javax.persistence.criteria.Order> orderList = new ArrayList<>();
        if (root == null || CollectionUtils.isEmpty(orders)) {
            return orderList;
        }
        for (MyOrder order : orders) {
            if (order == null) {
                continue;
            }
            String property = order.getProperty();
            Sort.Direction direction = order.getDirection();
            Path<?> path = this.getPath(root, property);
            if (path == null || direction == null) {
                continue;
            }
            switch (direction) {
                case ASC:
                    orderList.add(cb.asc(path));
                    break;
                case DESC:
                    orderList.add(cb.desc(path));
                    break;
                default:
                    break;
            }
        }
        return orderList;
    }

    /**
     * 获取Path
     */
    private <X> Path<?> getPath(Path<X> path, String propertyPath) {
        if (path == null || StringUtils.isEmpty(propertyPath)) {
            return path;
        }
        String property = StringUtils.substringBefore(propertyPath, PROPERTY_SEPARATOR);
        return getPath(path.get(property), StringUtils.substringAfter(propertyPath, PROPERTY_SEPARATOR));
    }
}
