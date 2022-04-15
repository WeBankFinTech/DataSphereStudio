package com.webank.wedatasphere.dss.framework.appconn.service.impl;

import com.webank.wedatasphere.dss.appconn.core.AppConn;
import com.webank.wedatasphere.dss.framework.appconn.exception.AppConnQualityErrorException;
import com.webank.wedatasphere.dss.framework.appconn.service.AppConnQualityChecker;

import java.util.function.Predicate;

/**
 * @author enjoyyin
 * @date 2022-04-14
 * @since 1.1.0
 */
public abstract class AbstractAppConnQualityChecker implements AppConnQualityChecker {

    protected void checkAppInstance(AppConn appConn) throws AppConnQualityErrorException {
        if(appConn.getAppDesc().getAppInstances().isEmpty()) {
            throw new AppConnQualityErrorException(10005, getErrorMsg(appConn.getAppDesc().getAppName(),
                    "no appInstance is found."));
        }
    }

    protected void checkNull(Object object, String appConnName, String objectStr) throws AppConnQualityErrorException {
        if(object == null) {
            throw new AppConnQualityErrorException(10005, errorMsg(appConnName, objectStr));
        }
    }

    protected void conditionCheck(Predicate<Void> condition, String appConnName, String reason) throws AppConnQualityErrorException {
        if(condition.test(null)) {
            throw new AppConnQualityErrorException(10005, getErrorMsg(appConnName, reason));
        }
    }

    protected String errorMsg(String appConnName, String object) {
        return getErrorMsg(appConnName, object + " is not exists.");
    }

    protected String getErrorMsg(String appConnName, String reason) {
        return String.format("check the quality of AppConn %s failed, reason: %s.", appConnName, reason);
    }

}
