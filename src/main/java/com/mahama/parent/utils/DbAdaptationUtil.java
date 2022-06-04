package com.mahama.parent.utils;

public class DbAdaptationUtil {
    private static final String db=SpringBeanUtil.getProperty("spring.datasource.druid.filter.stat.db-type");
    public static String adaptation(String value){
        switch (db) {
            case "sqlserver":
            case "sqlserver2005":
                value = value.replaceAll("\\[", "[[]");
                break;
            default:
                break;
        }
        return value;
    }
}
