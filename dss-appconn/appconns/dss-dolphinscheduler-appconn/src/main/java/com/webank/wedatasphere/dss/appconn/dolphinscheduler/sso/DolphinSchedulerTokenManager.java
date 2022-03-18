package com.webank.wedatasphere.dss.appconn.dolphinscheduler.sso;

import com.webank.wedatasphere.dss.appconn.dolphinscheduler.conf.DolphinSchedulerConf;
import com.webank.wedatasphere.dss.standard.app.sso.origin.client.HttpClient;
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestOperation;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;

/**
 * @author enjoyyin
 * @date 2022-03-17
 * @since 0.5.0
 */
public interface DolphinSchedulerTokenManager {

    void init(String baseUrl);

    void setSSORequestOperation(SSORequestOperation ssoRequestOperation);

    /**
     * 该 TokenManager 是否可以兼容 dsVersion 所传入的 DolphinScheduler 版本
     * @param dsVersion DolphinScheduler 版本
     * @return 如果兼容，返回true
     */
    boolean isCompatible(String dsVersion);

    String getToken(String userName);

    long getTokenExpireTime(String userName);

    int getUserId(String userName);

    String getBaseUrl();

    static DolphinSchedulerTokenManager getDolphinSchedulerTokenManager(String url) {
        // 一个DSS系统，只支持对接一个调度系统，所以这里只需要给出一个实例即可
        if(AbstractDolphinSchedulerTokenManager.dolphinSchedulerTokenManager != null) {
            return AbstractDolphinSchedulerTokenManager.dolphinSchedulerTokenManager;
        }
        synchronized (DolphinSchedulerTokenManager.class) {
            if(AbstractDolphinSchedulerTokenManager.dolphinSchedulerTokenManager != null) {
                return AbstractDolphinSchedulerTokenManager.dolphinSchedulerTokenManager;
            }
            String baseUrl = HttpClient.getBaseUrl(url);
            AbstractDolphinSchedulerTokenManager.dolphinSchedulerTokenManager = AbstractDolphinSchedulerTokenManager.dolphinSchedulerTokenManagers.stream()
                    .filter(tokenManager -> tokenManager.isCompatible(DolphinSchedulerConf.DS_VERSION.getValue()))
                    .findAny().orElseThrow(() -> new ExternalOperationFailedException(90304, "Cannot find a suitable DolphinSchedulerTokenManager for DolphinScheduler version "
                            + DolphinSchedulerConf.DS_VERSION.getValue()));
            AbstractDolphinSchedulerTokenManager.dolphinSchedulerTokenManager.init(baseUrl);
        }
        return AbstractDolphinSchedulerTokenManager.dolphinSchedulerTokenManager;
    }

}
