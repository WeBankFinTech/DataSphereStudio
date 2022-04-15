package com.webank.wedatasphere.dss.framework.appconn.service.impl;

import com.webank.wedatasphere.dss.appconn.core.AppConn;
import com.webank.wedatasphere.dss.framework.appconn.exception.AppConnQualityErrorException;
import com.webank.wedatasphere.dss.framework.appconn.service.AppConnQualityChecker;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

/**
 * @author enjoyyin
 * @date 2022-04-14
 * @since 1.1.0
 */
public abstract class AbstractAppConnQualityChecker implements AppConnQualityChecker {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private List<String> ignoreAppConnNameList;

    public AbstractAppConnQualityChecker(List<String> ignoreAppConnNameList) {
        this.ignoreAppConnNameList = ignoreAppConnNameList;
    }

    public AbstractAppConnQualityChecker(String ignoreAppConnNameStr) {
        if(StringUtils.isNotBlank(ignoreAppConnNameStr)) {
            this.ignoreAppConnNameList = Arrays.asList(ignoreAppConnNameStr.split(","));
        }
    }

    @Override
    public void checkQuality(AppConn appConn) throws AppConnQualityErrorException {
        if(ignoreAppConnNameList != null && ignoreAppConnNameList.contains(appConn.getAppDesc().getAppName())) {
            logger.info("ignore the quality checker of AppConn {}.", appConn.getAppDesc().getAppName());
            return;
        }
        checkAppConnQuality(appConn);
    }

    protected abstract void checkAppConnQuality(AppConn appConn) throws AppConnQualityErrorException;

    protected void checkAppInstance(AppConn appConn) throws AppConnQualityErrorException {
        if(appConn.getAppDesc().getAppInstances().isEmpty()) {
            throw new AppConnQualityErrorException(10005, getErrorMsg(appConn.getAppDesc().getAppName(),
                    "no appInstance is found in DSS database."));
        }
    }

    protected void checkNull(Object object, String appConnName, String objectStr) throws AppConnQualityErrorException {
        if(object == null) {
            throw new AppConnQualityErrorException(10005, errorMsg(appConnName, objectStr));
        }
    }

    protected void checkBoolean(Boolean condition, String appConnName, String reason) throws AppConnQualityErrorException {
        if(condition) {
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
