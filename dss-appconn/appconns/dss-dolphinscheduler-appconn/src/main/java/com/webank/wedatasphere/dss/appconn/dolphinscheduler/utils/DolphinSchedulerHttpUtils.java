package com.webank.wedatasphere.dss.appconn.dolphinscheduler.utils;

import com.webank.wedatasphere.dss.appconn.dolphinscheduler.ref.DolphinSchedulerResponseRefBuilder;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.sso.DolphinSchedulerTokenManager;
import com.webank.wedatasphere.dss.standard.app.sso.origin.request.action.DSSGetAction;
import com.webank.wedatasphere.dss.standard.app.sso.origin.request.action.DSSHttpAction;
import com.webank.wedatasphere.dss.standard.app.sso.origin.request.action.DSSPostAction;
import com.webank.wedatasphere.dss.standard.app.sso.origin.request.action.DSSPutAction;
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestOperation;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRefImpl;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import org.apache.http.HttpStatus;
import org.apache.linkis.httpclient.response.HttpResult;

import java.util.Map;

/**
 * @author enjoyyin
 * @date 2022-03-17
 * @since 0.5.0
 */
public class DolphinSchedulerHttpUtils {

    public static <T extends ResponseRefImpl> T getHttpResult(SSORequestOperation ssoRequestOperation, DSSHttpAction action) {
        HttpResult httpResult = (HttpResult) ssoRequestOperation.requestWithSSO(null, action);
        String responseBody = httpResult.getResponseBody();
        int httpStatusCode = httpResult.getStatusCode();
        if (HttpStatus.SC_OK != httpStatusCode && HttpStatus.SC_CREATED != httpStatusCode) {
            throw new ExternalOperationFailedException(90051, "request to DolphinScheduler with url " + action.getURL()
                    + " for user " + action.getUser() + " failed, the response http status code is " + httpStatusCode);
        }
        return (T) DolphinSchedulerResponseRefBuilder.newBuilder().setResponseBody(responseBody).build();
    }

    public static <T extends ResponseRefImpl> T getHttpResult(SSORequestOperation ssoRequestOperation, DSSHttpAction action, String url, String user) {
        action.setUrl(url);
        action.setUser(user);
        action.addHeader("token", DolphinSchedulerTokenManager.getDolphinSchedulerTokenManager(url).getToken(user));
        return getHttpResult(ssoRequestOperation, action);
    }

    public static <T extends ResponseRefImpl> T getHttpGetResult(SSORequestOperation ssoRequestOperation, String url, String user) {
        return getHttpResult(ssoRequestOperation, new DSSGetAction(), url, user);
    }

    public static <T extends ResponseRefImpl> T getHttpGetResult(SSORequestOperation ssoRequestOperation, String url, String user, Map<String, Object> parameters) {
        DSSGetAction getAction = new DSSGetAction();
        parameters.forEach(getAction::setParameter);
        return getHttpResult(ssoRequestOperation, getAction, url, user);
    }

    public static <T extends ResponseRefImpl> T getHttpPostResult(SSORequestOperation ssoRequestOperation, String url, String user, Map<String, Object> formData) {
        DSSPostAction postAction = new DSSPostAction();
        formData.forEach(postAction::setFormParam);
        return getHttpResult(ssoRequestOperation, postAction, url, user);
    }

    public static <T extends ResponseRefImpl> T getHttpPutResult(SSORequestOperation ssoRequestOperation, String url, String user, Map<String, Object> formData) {
        DSSPutAction postAction = new DSSPutAction();
        formData.forEach(postAction::setFormParam);
        return getHttpResult(ssoRequestOperation, postAction, url, user);
    }

}