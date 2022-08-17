package com.mahama.parent.vo;

import com.mahama.parent.factory.RetCodeFactory;

import java.io.Serializable;

public class Rets implements Serializable {
    public static <T> Ret<T> success(T data) {
        return new Ret<>(RetCodeFactory.SUCCESS(), "成功", data);
    }

    public static <T> Ret<T> success(T data, String msg) {
        return new Ret<>(RetCodeFactory.SUCCESS(), msg, data);
    }

    public static <T> Ret<T> failure(String msg) {
        return new Ret<>(RetCodeFactory.FAILURE(), msg, null);
    }

    public static <T> Ret<T> failure(Integer rc, String msg) {
        return new Ret<>(rc, msg, null);
    }

    public static <T> Ret<T> success() {
        return new Ret<>(RetCodeFactory.SUCCESS(), "成功", null);
    }

    public static <T> Ret<T> expire() {
        return new Ret<>(RetCodeFactory.EXPIRE(), "鉴权无效或过期", null);
    }
}
