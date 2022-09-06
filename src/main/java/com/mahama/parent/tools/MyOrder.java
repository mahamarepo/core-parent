package com.mahama.parent.tools;

import org.springframework.data.domain.Sort;

public class MyOrder {
    private String property;
    private Sort.Direction direction = Sort.Direction.ASC;

    /**
     * 构造方法
     */
    public MyOrder() {
    }

    /**
     * 构造方法
     *
     * @param property  属性
     * @param direction 方向
     */
    public MyOrder(String property, Sort.Direction direction) {
        this.property = property;
        this.direction = direction;
    }

    /**
     * 返回递增排序
     *
     * @param property 属性
     * @return 递增排序
     */
    public static MyOrder asc(String property) {
        return new MyOrder(property, Sort.Direction.ASC);
    }

    /**
     * 返回递减排序
     *
     * @param property 属性
     * @return 递减排序
     */
    public static MyOrder desc(String property) {
        return new MyOrder(property, Sort.Direction.DESC);
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
