package com.webank.wedatasphere.dss.standard.app.sso.operation;

import com.webank.wedatasphere.dss.common.utils.DSSCommonUtils;
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestOperation;
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestService;
import com.webank.wedatasphere.dss.standard.common.app.AppIntegrationService;
import com.webank.wedatasphere.dss.standard.common.entity.ref.RequestRef;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.service.Operation;

/**
 * @author enjoyyin
 * @date 2022-03-09
 * @since 0.5.0
 */
public abstract class AbstractOperation<K extends RequestRef, V extends ResponseRef>
        implements Operation<K, V> {

    protected SSORequestOperation ssoRequestOperation;
    protected AppIntegrationService<SSORequestService> service;

    /**
     * This method is used to create a SSORequestOperation.
     * If the third-part AppConn wants to use SSORequestOperation which dependents the HttpClient of Linkis
     * to request the third-part system, please override this method. Otherwise, the operations of
     * third-part AppConn has no necessity to override this method.
     * @return the appConn name
     */
    protected abstract String getAppConnName();

    @Override
    public void init() {
        if(getAppConnName() != null) {
            this.ssoRequestOperation = service.getSSORequestService().createSSORequestOperation(getAppConnName());
        }
    }

    protected String getBaseUrl() {
        return service.getAppInstance().getBaseUrl();
    }

    protected String mergeUrl(String url, String suffix) {
        if(url.endsWith("/")) {
            return url + suffix;
        } else {
            return url + "/" + suffix;
        }
    }

    protected String mergeBaseUrl(String suffix) {
        return mergeUrl(getBaseUrl(), suffix);
    }

    protected String toJson(Object object) {
        if(object == null) {
            return null;
        } else {
            return DSSCommonUtils.COMMON_GSON.toJson(object);
        }
    }

}
