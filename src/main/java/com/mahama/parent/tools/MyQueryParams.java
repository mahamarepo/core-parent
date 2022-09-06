package com.mahama.parent.tools;

import java.util.Collection;

public class MyQueryParams<V> {
    MyOperator operator;
    String property;
    V value;

    public MyQueryParams(String property, MyOperator operator, V value) {
        this.operator = operator;
        this.property = property;
        this.value = value;
    }

    public MyQueryParams(String property, MyOperator operator) {
        this.operator = operator;
        this.property = property;
    }

    /**
     * 相等
     */
    public static <V> MyQueryParams<V> eq(String property, V value) {
        return new MyQueryParams<>(property, MyOperator.eq, value);
    }

    /**
     * 相等
     */
    public static <T, V> MyQueryParams<V> eq(MyFunction<T, V> func, V value) {
        return new MyQueryParams<>(func.getFieldName(), MyOperator.eq, value);
    }

    /**
     * 不相等
     */
    public static <V> MyQueryParams<V> ne(String property, V value) {
        return new MyQueryParams<>(property, MyOperator.ne, value);
    }

    /**
     * 不相等
     */
    public static <T, V> MyQueryParams<V> ne(MyFunction<T, V> func, V value) {
        return new MyQueryParams<>(func.getFieldName(), MyOperator.ne, value);
    }

    /**
     * 大于
     */
    public static <V> MyQueryParams<V> gt(String property, V value) {
        return new MyQueryParams<>(property, MyOperator.gt, value);
    }

    /**
     * 大于
     */
    public static <T, V> MyQueryParams<V> gt(MyFunction<T, V> func, V value) {
        return new MyQueryParams<>(func.getFieldName(), MyOperator.gt, value);
    }

    /**
     * 小于
     */
    public static <V> MyQueryParams<V> lt(String property, V value) {
        return new MyQueryParams<>(property, MyOperator.lt, value);
    }

    /**
     * 小于
     */
    public static <T, V> MyQueryParams<V> lt(MyFunction<T, V> func, V value) {
        return new MyQueryParams<>(func.getFieldName(), MyOperator.lt, value);
    }

    /**
     * 大于等于
     */
    public static <V> MyQueryParams<V> ge(String property, V value) {
        return new MyQueryParams<>(property, MyOperator.ge, value);
    }

    /**
     * 大于等于
     */
    public static <T, V> MyQueryParams<V> ge(MyFunction<T, V> func, V value) {
        return new MyQueryParams<>(func.getFieldName(), MyOperator.ge, value);
    }


    /**
     * 小于等于
     */
    public static <V> MyQueryParams<V> le(String property, V value) {
        return new MyQueryParams<>(property, MyOperator.le, value);
    }

    /**
     * 小于等于
     */
    public static <T, V> MyQueryParams<V> le(MyFunction<T, V> func, V value) {
        return new MyQueryParams<>(func.getFieldName(), MyOperator.le, value);
    }


    /**
     * like %%
     */
    public static MyQueryParams<String> like(String property, String value) {
        return new MyQueryParams<>(property, MyOperator.like, value);
    }

    /**
     * like %%
     */
    public static <T> MyQueryParams<String> like(MyFunction<T, String> func, String value) {
        return new MyQueryParams<>(func.getFieldName(), MyOperator.like, value);
    }

    /**
     * 右like xxx%
     */
    public static MyQueryParams<String> rlike(String property, String value) {
        return new MyQueryParams<>(property, MyOperator.rLike, value);
    }

    /**
     * 右like xxx%
     */
    public static <T> MyQueryParams<String> rLike(MyFunction<T, String> func, String value) {
        return new MyQueryParams<>(func.getFieldName(), MyOperator.rLike, value);
    }

    /**
     * 左like %xxx
     */
    public static MyQueryParams<String> lLike(String property, String value) {
        return new MyQueryParams<>(property, MyOperator.lLike, value);
    }

    /**
     * 左like %xxx
     */
    public static <T> MyQueryParams<String> lLike(MyFunction<T, String> func, String value) {
        return new MyQueryParams<>(func.getFieldName(), MyOperator.lLike, value);
    }

    /**
     * 自定义模糊
     */
    public static MyQueryParams<String> iLike(String property, String value) {
        return new MyQueryParams<>(property, MyOperator.iLike, value);
    }

    /**
     * 自定义模糊
     */
    public static <T> MyQueryParams<String> iLike(MyFunction<T, String> func, String value) {
        return new MyQueryParams<>(func.getFieldName(), MyOperator.iLike, value);
    }

    /**
     * 集合中
     */
    public static <V extends Collection<?>> MyQueryParams<V> in(String property, V value) {
        return new MyQueryParams<>(property, MyOperator.in, value);
    }

    /**
     * 集合中
     */
    public static <T, V extends Collection<?>> MyQueryParams<V> in(MyFunction<T, V> func, V value) {
        return new MyQueryParams<>(func.getFieldName(), MyOperator.in, value);
    }

    /**
     * 不在集合中
     */
    public static <V extends Collection<?>> MyQueryParams<V> notIn(String property, V value) {
        return new MyQueryParams<>(property, MyOperator.notIn, value);
    }

    /**
     * 不在集合中
     */
    public static <T, V extends Collection<?>> MyQueryParams<V> notIn(MyFunction<T, V> func, V value) {
        return new MyQueryParams<>(func.getFieldName(), MyOperator.notIn, value);
    }

    /**
     * 是空
     */
    public static MyQueryParams<String> isNull(String property) {
        return new MyQueryParams<>(property, MyOperator.isNull);
    }

    /**
     * 是空
     */
    public static <T,V> MyQueryParams<String> isNull(MyFunction<T, V> func) {
        return new MyQueryParams<>(func.getFieldName(), MyOperator.isNull);
    }

    /**
     * 不是空
     */
    public static MyQueryParams<String> isNotNull(String property) {
        return new MyQueryParams<>(property, MyOperator.isNotNull);
    }

    /**
     * 不是空
     */
    public static <T,V> MyQueryParams<String> isNotNull(MyFunction<T, V> func) {
        return new MyQueryParams<>(func.getFieldName(), MyOperator.isNotNull);
    }
}
