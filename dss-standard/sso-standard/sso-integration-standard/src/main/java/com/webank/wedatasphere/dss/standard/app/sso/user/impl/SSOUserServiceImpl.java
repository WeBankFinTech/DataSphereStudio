package com.webank.wedatasphere.dss.standard.app.sso.user.impl;

import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestService;
import com.webank.wedatasphere.dss.standard.app.sso.user.*;
import com.webank.wedatasphere.dss.standard.common.app.AppSingletonIntegrationServiceImpl;
import com.webank.wedatasphere.dss.standard.common.utils.AppStandardClassUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author enjoyyin
 * @date 2022-04-26
 * @since 1.1.0
 */
public class SSOUserServiceImpl extends AppSingletonIntegrationServiceImpl<SSOUserOperation, SSORequestService>
        implements SSOUserService {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private String appConnName;

    public void setAppConnName(String appConnName) {
        this.appConnName = appConnName;
    }

    public String getAppConnName() {
        return appConnName;
    }

    protected <T extends SSOUserOperation> T createSSOUserOperation(Class<T> clazz) {
        List<Class<? extends T>> classes = AppStandardClassUtils.getInstance(appConnName).getClasses(clazz);
        if(CollectionUtils.isEmpty(classes)) {
            logger.info("no implementation find for {}, ignore to sync user to AppConn {}.", clazz.getSimpleName(), appConnName);
            return null;
        }
        T instance = AppStandardClassUtils.getInstance(appConnName).getInstanceOrWarn(clazz);
        logger.info("find a implementation {} for {}, ignore to sync user to AppConn {}.", instance.getClass().getName(),
                clazz.getSimpleName(), appConnName);
        return instance;
    }

    @Override
    public SSOUserCreationOperation getSSOUserCreationOperation() {
        return getOrCreate(() -> createSSOUserOperation(SSOUserCreationOperation.class), SSOUserCreationOperation.class);
    }

    @Override
    public SSOUserUpdateOperation getSSOUserUpdateOperation() {
        return getOrCreate(() -> createSSOUserOperation(SSOUserUpdateOperation.class), SSOUserUpdateOperation.class);
    }

    @Override
    public SSOUserGetOperation getSSOUserGetOperation() {
        return getOrCreate(() -> createSSOUserOperation(SSOUserGetOperation.class), SSOUserGetOperation.class);
    }

    @Override
    public SSOUserDeletionOperation getSSOUserDeletionOperation() {
        return getOrCreate(() -> createSSOUserOperation(SSOUserDeletionOperation.class), SSOUserDeletionOperation.class);
    }

    @Override
    protected void initOperation(SSOUserOperation operation) {
        super.initOperation(operation);
        operation.setSSOUserService(this);
    }
}
