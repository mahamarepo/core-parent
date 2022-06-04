package com.mahama.parent.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

import java.util.function.Function;

@Getter
@Setter
public class JwtEvent extends ApplicationEvent {
    private Function<String, Long> apiIdFunction;
    private Function<String, Long> tenantIdFunction;
    private Function<String, Long> userIdFunction;

    public Long getApiId() {
        if (apiIdFunction == null)
            return null;
        return apiIdFunction.apply(null);
    }

    public Long getTenantId() {
        return getTenantId(null);
    }

    public Long getTenantId(String token) {
        if (tenantIdFunction == null)
            return null;
        return tenantIdFunction.apply(token);
    }

    public Long getUserId() {
        return getUserId(null);
    }

    public Long getUserId(String token) {
        if (userIdFunction == null)
            return null;
        return userIdFunction.apply(token);
    }

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public JwtEvent(Object source) {
        super(source);
    }
}
