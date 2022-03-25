package com.webank.wedatasphere.dss.appconn.dolphinscheduler.utils;

import com.webank.wedatasphere.dss.appconn.dolphinscheduler.conf.DolphinSchedulerConf;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.ref.DolphinSchedulerResponseRefBuilder;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.sso.DolphinSchedulerTokenManager;
import com.webank.wedatasphere.dss.standard.app.sso.origin.request.action.DSSGetAction;
import com.webank.wedatasphere.dss.standard.app.sso.origin.request.action.DSSHttpAction;
import com.webank.wedatasphere.dss.standard.app.sso.origin.request.action.DSSPostAction;
import com.webank.wedatasphere.dss.standard.app.sso.origin.request.action.DSSPutAction;
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestOperation;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRefImpl;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.HttpStatus;
import org.apache.linkis.httpclient.response.HttpResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @author enjoyyin
 * @date 2022-03-17
 * @since 0.5.0
 */
public class DolphinSchedulerHttpUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(DolphinSchedulerHttpUtils.class);

    public static <T extends ResponseRefImpl> T getHttpResult(SSORequestOperation ssoRequestOperation, DSSHttpAction action) {
        HttpResult httpResult;
        try {
            httpResult = (HttpResult) ssoRequestOperation.requestWithSSO(null, action);
        } catch (Exception e) {
            LOGGER.error("user {} send request to dolphinscheduler in url {} failed, the requestBody is {}.", action.getUser(), action.getURL(), action.getRequestBody());
            throw new ExternalOperationFailedException(90322, "send request to dolphinscheduler failed. Caused by: " + ExceptionUtils.getRootCauseMessage(e), e);
        }
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
        formData.forEach(postAction::setParameter);
        return getHttpResult(ssoRequestOperation, postAction, url, user);
    }

    public static <T extends ResponseRefImpl> T getHttpPutResult(SSORequestOperation ssoRequestOperation, String url, String user, Map<String, Object> formData) {
        DSSPutAction postAction = new DSSPutAction();
        formData.forEach(postAction::addRequestPayload);
        return getHttpResult(ssoRequestOperation, postAction, url, user);
    }

    public static String getDolphinSchedulerBaseUrl(String baseUrl) {
        if(StringUtils.isNotBlank(DolphinSchedulerConf.DOLPHIN_SCHEDULER_URI_PREFIX.getValue())) {
            return baseUrl.endsWith("/") ? baseUrl + DolphinSchedulerConf.DOLPHIN_SCHEDULER_URI_PREFIX.getValue() :
                    baseUrl + "/" + DolphinSchedulerConf.DOLPHIN_SCHEDULER_URI_PREFIX.getValue();
        } else {
            return baseUrl;
        }
    }

    public static long parseToLong(Object val) {
        if (val instanceof Double) {
            return ((Double) val).longValue();
        } else if (val instanceof Integer) {
            return new Double((Integer) val).longValue();
        } else if( val instanceof Long) {
            return (Long) val;
        } else if(val != null) {
            return Long.parseLong(val.toString());
        }
        throw new ExternalOperationFailedException(90322, "parse the return of DolphinScheduler failed, the value is null.");
    }

}
