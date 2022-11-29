package com.mahama.parent.vo;

import com.mahama.parent.factory.RetFactory;

import java.io.Serializable;

public class Rets implements Serializable {
    public static <T> Ret<T> success(T data) {
        return new Ret<>(RetFactory.SUCCESS(), "成功", data);
    }

    public static <T> Ret<T> success(T data, String msg) {
        return new Ret<>(RetFactory.SUCCESS(), msg, data);
    }

    public static <T> Ret<T> failure(String msg) {
        return new Ret<>(RetFactory.FAILURE(), msg, null);
    }

    public static <T> Ret<T> failure(Integer rc, String msg) {
        return new Ret<>(rc, msg, null);
    }

    public static <T> Ret<T> success() {
        return new Ret<>(RetFactory.SUCCESS(), "成功", null);
    }

    public static <T> Ret<T> expire() {
        return expire("鉴权无效或过期");
    }

    public static <T> Ret<T> expire(String msg) {
        return new Ret<>(RetFactory.EXPIRE(), msg, null);
    }
}
