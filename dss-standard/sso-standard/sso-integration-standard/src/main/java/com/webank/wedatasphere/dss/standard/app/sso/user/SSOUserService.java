package com.webank.wedatasphere.dss.standard.app.sso.user;

import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestService;
import com.webank.wedatasphere.dss.standard.common.app.AppIntegrationService;

/**
 * DSS 用户与第三方 AppConn 的用户同步服务
 * @author enjoyyin
 * @date 2022-04-25
 * @since 1.1.0
 */
public interface SSOUserService extends AppIntegrationService<SSORequestService> {

    /**
     * 用于请求第三方 AppConn 创建同名用户
     * @return
     */
    SSOUserCreationOperation getSSOUserCreationOperation();

    /**
     * 用于修改第三方 AppConn 用户的基础信息
     * @return SSOUserUpdateOperation
     */
    SSOUserUpdateOperation getSSOUserUpdateOperation();

    /**
     * 用于请求第三方 AppConn 获取同名用户信息
     * @return
     */
    SSOUserGetOperation getSSOUserGetOperation();

    /**
     * 预留接口，用于删除第三方 AppConn 用户
     * @return
     */
    SSOUserDeletionOperation getSSOUserDeletionOperation();

}
