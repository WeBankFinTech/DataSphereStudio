package com.webank.wedatasphere.dss.datamodel.center.common.context;


public interface DataModelSecurityContextHolderStrategy {

    void clearContext();

    /**
     * Obtains the current context.
     * @return a context (never <code>null</code> - create a default implementation if
     * necessary)
     */
    DataModelSecurityContext getContext();

    /**
     * Sets the current context.
     * @param context to the new argument (should never be <code>null</code>, although
     * implementations must check if <code>null</code> has been passed and throw an
     * <code>IllegalArgumentException</code> in such cases)
     */
    void setContext(DataModelSecurityContext context);

    /**
     * Creates a new, empty context implementation, for use by
     * <tt>SecurityContextRepository</tt> implementations, when creating a new context for
     * the first time.
     * @return the empty context.
     */
    DataModelSecurityContext createEmptyContext();
}
