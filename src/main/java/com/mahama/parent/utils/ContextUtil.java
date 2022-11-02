package com.mahama.parent.utils;

import com.mahama.corecommon.context.ConfigContext;

public class ContextUtil {
    private static final ThreadLocal<ConfigContext> threadConfigContext = new ThreadLocal<>();

    public static void setConfigContext(ConfigContext ctx) {
        threadConfigContext.set(ctx);
    }

    public static ConfigContext getConfigContext() {
        ConfigContext ctx = threadConfigContext.get();
        if (ctx == null) {
            ctx = resetAllConfig();
        }
        return ctx;
    }

    public static ConfigContext resetAllConfig() {
        ConfigContext ctx = new ConfigContext();
        ctx.setIgnoreTenant(false);
        ctx.setIgnoreTenantTableList(null);
        setConfigContext(ctx);
        return ctx;
    }
}
