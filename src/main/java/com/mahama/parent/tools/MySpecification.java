package com.mahama.parent.tools;

import lombok.Data;
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
 * QueryParams.like("id", id),
 * QueryParams.like("Name",Name)).asc("id"), pr);
 */
public class MySpecification<T> implements Specification<T> {
    @Data
    static class MyQuery {
        QueryType type;
        Collection<QueryParams<?>> list;
    }

    private final List<MyQuery> queryList = new ArrayList<>();

    //属性分隔
    private static final String PROPERTY_SEPARATOR = ".";

    //排序
    List<Order> orders = new ArrayList<>();

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
        Predicate restrictions = cb.and(getPredicates(root, cb));
        cq.orderBy(getOrders(root, cb));
        return restrictions;
    }

    public Specification<T> toSpecification() {
        return this;
    }

    public MySpecification<T> and(Collection<QueryParams<?>> conditions) {
        MyQuery query = new MyQuery();
        query.setType(QueryType.AND);
        query.setList(conditions);
        queryList.add(query);
        return this;
    }

    public MySpecification<T> and(QueryParams<?>... conditions) {
        MyQuery query = new MyQuery();
        query.setType(QueryType.AND);
        query.setList(Arrays.asList(conditions));
        queryList.add(query);
        return this;
    }

    public MySpecification<T> or(Collection<QueryParams<?>> conditions) {
        MyQuery query = new MyQuery();
        query.setType(QueryType.OR);
        query.setList(conditions);
        queryList.add(query);
        return this;
    }

    public MySpecification<T> or(QueryParams<?>... conditions) {
        MyQuery query = new MyQuery();
        query.setType(QueryType.OR);
        query.setList(Arrays.asList(conditions));
        queryList.add(query);
        return this;
    }

    public MySpecification<T> desc(String property) {
        this.orders.add(Order.desc(property));
        return this;
    }

    public MySpecification<T> asc(String property) {
        this.orders.add(Order.asc(property));
        return this;
    }

    public MySpecification<T> order(String property, Sort.Direction direction) {
        this.orders.add(new Order(property, direction));
        return this;
    }

    public MySpecification<T> orders(Order... orders) {
        this.orders.addAll(Arrays.asList(orders));
        return this;
    }

    public MySpecification<T> orders(Collection<Order> orders) {
        this.orders.addAll(orders);
        return this;
    }

    private Predicate[] andPredicates(Root<T> root, CriteriaBuilder cb, Collection<QueryParams<?>> list) {
        List<Predicate> listAnd = new ArrayList<>();
        for (QueryParams<?> condition : list) {
            if (condition == null) {
                continue;
            }

            Path<?> path = this.getPath(root, condition.property);
            if (path == null) {
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
                    listAnd.add(path.in(condition.value));
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
                    listAnd.add(path.in(condition.value).not());
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
        Predicate restrictions = cb.conjunction();
        for (MyQuery query : queryList) {
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
        for (Order order : orders) {
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

    /**
     * 运算符
     */
    public enum Operator {

        /**
         * 等于
         */
        eq(" = "),

        /**
         * 不等于
         */
        ne(" != "),

        /**
         * 大于
         */
        gt(" > "),

        /**
         * 小于
         */
        lt(" < "),

        /**
         * 大于等于
         */
        ge(" >= "),

        /**
         * 小于等于
         */
        le(" <= "),

        /**
         * like %%
         */
        like(" like "),

        /**
         * 右like xxx%
         */
        rLike("like "),

        /**
         * 左like %xxx
         */
        lLike(" like "),

        /**
         * 自定义模糊匹配
         */
        iLike(" like "),

        /**
         * 包含
         */
        in(" in "),

        /**
         * 包含
         */
        notIn(" not in "),

        /**
         * 为Null
         */
        isNull(" is NULL "),

        /**
         * 不为Null
         */
        isNotNull(" is not NULL ");

        private String operator;

        Operator(String operator) {
            this.operator = operator;
        }

        public String getOperator() {
            return operator;
        }

        public void setOperator(String operator) {
            this.operator = operator;
        }
    }

    public enum QueryType {
        AND,
        OR
    }

    /**
     * 条件
     */
    public static class QueryParams<V> {

        Operator operator;
        String property;
        V value;

        public QueryParams(String property, Operator operator, V value) {
            this.operator = operator;
            this.property = property;
            this.value = value;
        }

        public QueryParams(String property, Operator operator) {
            this.operator = operator;
            this.property = property;
        }

        /**
         * 相等
         */
        public static <V> QueryParams<V> eq(String property, V value) {
            return new QueryParams<>(property, Operator.eq, value);
        }

        /**
         * 不相等
         */
        public static <V> QueryParams<V> ne(String property, V value) {
            return new QueryParams<>(property, Operator.ne, value);
        }

        /**
         * 大于
         */
        public static <V> QueryParams<V> gt(String property, V value) {
            return new QueryParams<>(property, Operator.gt, value);
        }

        /**
         * 小于
         */
        public static <V> QueryParams<V> lt(String property, V value) {
            return new QueryParams<>(property, Operator.lt, value);
        }

        /**
         * 大于等于
         */
        public static <V> QueryParams<V> ge(String property, V value) {
            return new QueryParams<>(property, Operator.ge, value);
        }

        /**
         * 小于等于
         */
        public static <V> QueryParams<V> le(String property, V value) {
            return new QueryParams<>(property, Operator.le, value);
        }


        /**
         * like %%
         */
        public static QueryParams<String> like(String property, String value) {
            return new QueryParams<>(property, Operator.like, value);
        }

        /**
         * 右like xxx%
         */
        public static QueryParams<String> rlike(String property, String value) {
            return new QueryParams<>(property, Operator.rLike, value);
        }

        /**
         * 左like %xxx
         */
        public static QueryParams<String> llike(String property, String value) {
            return new QueryParams<>(property, Operator.lLike, value);
        }

        /**
         * 自定义模糊
         */
        public static QueryParams<String> ilike(String property, String value) {
            return new QueryParams<>(property, Operator.iLike, value);
        }

        /**
         * 集合中
         */
        public static <V extends Collection<?>> QueryParams<V> in(String property, V value) {
            return new QueryParams<>(property, Operator.in, value);
        }

        /**
         * 不在集合中
         */
        public static <V extends Collection<?>> QueryParams<V> notIn(String property, V value) {
            return new QueryParams<>(property, Operator.notIn, value);
        }

        /**
         * 是空
         */
        public static QueryParams<String> isNull(String property) {
            return new QueryParams<>(property, Operator.isNull);
        }

        /**
         * 不是空
         */
        public static QueryParams<String> isNotNull(String property) {
            return new QueryParams<>(property, Operator.isNotNull);
        }

    }

    /**
     * 排序
     */
    public static class Order {
        private String property;
        private Sort.Direction direction = Sort.Direction.ASC;

        /**
         * 构造方法
         */
        public Order() {
        }

        /**
         * 构造方法
         *
         * @param property  属性
         * @param direction 方向
         */
        public Order(String property, Sort.Direction direction) {
            this.property = property;
            this.direction = direction;
        }

        /**
         * 返回递增排序
         *
         * @param property 属性
         * @return 递增排序
         */
        public static Order asc(String property) {
            return new Order(property, Sort.Direction.ASC);
        }

        /**
         * 返回递减排序
         *
         * @param property 属性
         * @return 递减排序
         */
        public static Order desc(String property) {
            return new Order(property, Sort.Direction.DESC);
        }


        @Override
        public String toString() {
            return property + " " + direction.name();
        }

        public Sort.Direction getDirection() {
            return direction;
        }

        public String getProperty() {
            return property;
        }
    }
}
