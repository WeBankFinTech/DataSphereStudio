package com.webank.wedatasphere.dss.datamodel.center.common.context;


public class DataModelSecurityContextHolder {

    private static DataModelSecurityContextHolderStrategy strategy;

    static {
        initialize();
    }

    private static void initialize(){
        strategy = new ThreadLocalSecurityContextHolderStrategy();
    }


    /**
     * Explicitly clears the context value from the current thread.
     */
    public static void clearContext() {
        strategy.clearContext();
    }

    /**
     * Obtain the current <code>SecurityContext</code>.
     * @return the security context (never <code>null</code>)
     */
    public static DataModelSecurityContext getContext() {
        return strategy.getContext();
    }

    public static void setContext(DataModelSecurityContext context) {
        strategy.setContext(context);
    }
}
