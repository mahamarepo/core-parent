package com.mahama.parent.factory;

import com.mahama.parent.config.RetCodeConfig;
import com.mahama.parent.utils.SpringBeanUtil;

public class RetCodeFactory {
    private static final RetCodeConfig codeConfig = SpringBeanUtil.getBean(RetCodeConfig.class);

    public static Integer SUCCESS() {
        return codeConfig.getSuccess();
    }

    public static Integer FAILURE() {
        return codeConfig.getFailure();
    }

    public static Integer EXPIRE() {
        return codeConfig.getExpire();
    }
}
