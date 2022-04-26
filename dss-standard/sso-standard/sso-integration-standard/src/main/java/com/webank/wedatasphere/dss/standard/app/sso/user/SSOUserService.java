package com.webank.wedatasphere.dss.standard.app.sso.user;

import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestService;
import com.webank.wedatasphere.dss.standard.common.app.AppIntegrationService;

/**
 * @author enjoyyin
 * @date 2022-04-25
 * @since 1.1.0
 */
public interface SSOUserService extends AppIntegrationService<SSORequestService> {

    SSOUserCreationOperation getSSOUserCreationOperation();

    /**
     * 预留接口，用于修改用户的基础信息
     * @return SSOUserUpdateOperation
     */
    SSOUserUpdateOperation getSSOUserUpdateOperation();

    SSOUserGetOperation getSSOUserGetOperation();

    SSOUserDeletionOperation getSSOUserDeletionOperation();

}
