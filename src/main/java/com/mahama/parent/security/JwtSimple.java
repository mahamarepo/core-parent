package com.mahama.parent.security;

import com.mahama.parent.utils.SpringBeanUtil;
import org.springframework.context.ApplicationEventPublisher;

/**
 * 简易Jwt
 */
public class JwtSimple {
    private static final ApplicationEventPublisher applicationEventPublisher = SpringBeanUtil.getApplicationEventPublisher();

    private static JwtEvent JwtEvent;

    public static JwtEvent getJwt() {
        if (JwtEvent == null) {
            JwtEvent = new JwtEvent(JwtSimple.class);
            applicationEventPublisher.publishEvent(JwtEvent);
        }
        return JwtEvent;
    }

    public static Long getApiId() {
        return getJwt().getApiId();
    }

    public static Long getTenantId() {
        return getJwt().getTenantId();
    }

    public static Long getTenantId(String token) {
        return getJwt().getTenantId(token);
    }

    public static Long getUserId() {
        return getJwt().getUserId();
    }

    public static Long getUserId(String token) {
        return getJwt().getUserId(token);
    }
}
