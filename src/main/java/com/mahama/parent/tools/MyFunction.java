package com.mahama.parent.tools;

import com.mahama.common.utils.StringUtil;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;

@FunctionalInterface
public interface MyFunction<T, R> extends Serializable {
    R apply(T t);

    default SerializedLambda getSerializedLambda() {
        //先检查缓存中是否已存在
        SerializedLambda lambda = null;
        try {//提取SerializedLambda并缓存
            Method method = this.getClass().getDeclaredMethod("writeReplace");
            method.setAccessible(Boolean.TRUE);
            lambda = (SerializedLambda) method.invoke(this);
        } catch (Exception e) {
            System.err.println("获取SerializedLambda异常, class=" + this.getClass().getSimpleName());
        }
        return lambda;
    }

    default String getFieldName() {
        SerializedLambda lambda = getSerializedLambda();
        String methodName = lambda.getImplMethodName();
        String prefix = null;
        if (methodName.startsWith("get")) {
            prefix = "get";
        } else if (methodName.startsWith("is")) {
            prefix = "is";
        }
        if (prefix == null) {
            System.err.println("无效的getter方法: " + methodName);
        }
        return StringUtil.firstCharToLowerCase(StringUtil.removePrefix(methodName, prefix));
    }
}
