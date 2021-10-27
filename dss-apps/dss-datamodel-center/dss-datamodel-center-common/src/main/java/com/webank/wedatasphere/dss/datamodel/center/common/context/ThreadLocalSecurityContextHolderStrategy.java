package com.webank.wedatasphere.dss.datamodel.center.common.context;


import org.springframework.util.Assert;

final class ThreadLocalSecurityContextHolderStrategy implements DataModelSecurityContextHolderStrategy{

    private static final ThreadLocal<DataModelSecurityContext> contextHolder = new ThreadLocal<>();

    @Override
    public void clearContext() {
        contextHolder.remove();
    }

    @Override
    public DataModelSecurityContext getContext() {
        DataModelSecurityContext ctx = contextHolder.get();
        if (ctx == null) {
            ctx = createEmptyContext();
            contextHolder.set(ctx);
        }
        return ctx;
    }

    @Override
    public void setContext(DataModelSecurityContext context) {
        Assert.notNull(context, "Only non-null SecurityContext instances are permitted");
        contextHolder.set(context);
    }

    @Override
    public DataModelSecurityContext createEmptyContext() {
        return new DataModelSecurityContext();
    }
}
