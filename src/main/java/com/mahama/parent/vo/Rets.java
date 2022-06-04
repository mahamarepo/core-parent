package com.mahama.parent.vo;

import java.io.Serializable;

public class Rets implements Serializable {

    public static final Integer SUCCESS = 20000;
    public static final Integer FAILURE = 99999;
    public static  final Integer TOKEN_EXPIRE=50014;

    public static <T> Ret<T> success(T data) {
        return new Ret<>(Rets.SUCCESS, "成功", data);
    }
    public static <T> Ret<T> success(T data,String msg) {
        return new Ret<>(Rets.SUCCESS, msg, data);
    }

    public static <T> Ret<T> failure(String msg) {
        return new Ret<>(Rets.FAILURE, msg, null);
    }

    public static <T> Ret<T> failure(Integer rc, String msg) {
        return new Ret<>(rc, msg, null);
    }

    public static <T> Ret<T> success() {
        return new Ret<>(Rets.SUCCESS, "成功", null);
    }
    public static <T> Ret<T> expire(){
        return new Ret<>(Rets.TOKEN_EXPIRE,"鉴权无效或过期",null);
    }
}
