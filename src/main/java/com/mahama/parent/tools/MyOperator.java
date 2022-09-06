package com.mahama.parent.tools;

public enum MyOperator {
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

    MyOperator(String operator) {
        this.operator = operator;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }
}
