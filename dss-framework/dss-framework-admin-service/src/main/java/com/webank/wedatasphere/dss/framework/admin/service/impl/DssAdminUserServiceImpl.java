package com.webank.wedatasphere.dss.framework.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.webank.wedatasphere.dss.appconn.core.ext.OnlySSOAppConn;
import com.webank.wedatasphere.dss.appconn.manager.AppConnManager;
import com.webank.wedatasphere.dss.common.utils.DSSExceptionUtils;
import com.webank.wedatasphere.dss.framework.admin.common.constant.UserConstants;
import com.webank.wedatasphere.dss.framework.admin.common.utils.StringUtils;
import com.webank.wedatasphere.dss.framework.admin.pojo.entity.DssAdminUser;
import com.webank.wedatasphere.dss.framework.admin.service.DssAdminUserService;
import com.webank.wedatasphere.dss.framework.admin.xml.DssUserMapper;
import com.webank.wedatasphere.dss.framework.common.exception.DSSFrameworkWarnException;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.standard.app.sso.user.SSOUserGetOperation;
import com.webank.wedatasphere.dss.standard.app.sso.user.SSOUserOperation;
import com.webank.wedatasphere.dss.standard.app.sso.user.SSOUserService;
import com.webank.wedatasphere.dss.standard.app.sso.user.ref.DSSUserContentRequestRef;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.utils.RequestRefUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;

import static com.webank.wedatasphere.dss.framework.admin.conf.AdminConf.SUPER_ADMIN_LIST;

@Service
public class DssAdminUserServiceImpl extends ServiceImpl<DssUserMapper, DssAdminUser> implements DssAdminUserService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    DssUserMapper dssUserMapper;

    /**
     * 校验用户名称是否唯一
     *
     * @param userName 用户名称
     * @return 结果
     */
    @Override
    public String checkUserNameUnique(String userName) {
        int count = dssUserMapper.checkUserNameUnique(userName);
        if (count > 0) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 校验用户名称是否唯一
     *
     * @param user 用户信息
     * @return
     */
    @Override
    public String checkPhoneUnique(DssAdminUser user) {
        Long userId = StringUtils.isNull(user.getId()) ? -1L : user.getId();
        DssAdminUser info = dssUserMapper.checkPhoneUnique(user.getPhonenumber());
        if (StringUtils.isNotNull(info) && info.getId().longValue() != userId.longValue()) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 校验email是否唯一
     *
     * @param user 用户信息
     * @return
     */
    @Override
    public String checkEmailUnique(DssAdminUser user) {
        Long userId = StringUtils.isNull(user.getId()) ? -1L : user.getId();
        DssAdminUser info = dssUserMapper.checkEmailUnique(user.getEmail());
        if (StringUtils.isNotNull(info) && info.getId().longValue() != userId.longValue()) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    @Override
    public void insertOrUpdateUser(String username, Workspace workspace) {
        DssAdminUser dssUser = dssUserMapper.selectUserByName(username);
        if (dssUser == null) {
            logger.info("new user {} access to DSS, now try to add it.", username);
            dssUser = new DssAdminUser();
            dssUser.setUserName(username);
            dssUser.setName(username);
            dssUser.setIsFirstLogin(1);
            dssUser.setIsAdmin(ArrayUtils.contains(SUPER_ADMIN_LIST, username) ? 1 : 0);
            dssUser.setLoginNum(1);
            insertUser(dssUser, workspace);
        } else {
            dssUser.setIsFirstLogin(0);
            dssUser.setLastLoginTime(new Date());
            dssUser.setLoginNum(dssUser.getLoginNum() == null ? 0 : dssUser.getLoginNum() + 1);
            dssUserMapper.updateUser(dssUser);
        }
    }

    @Override
    public void insertIfNotExist(String username, Workspace workspace) {
        DssAdminUser dssUser = dssUserMapper.selectUserByName(username);
        if (dssUser == null) {
            logger.info("new user {} access to DSS, now try to add it.", username);
            dssUser = new DssAdminUser();
            dssUser.setUserName(username);
            dssUser.setName(username);
            dssUser.setIsFirstLogin(1);
            dssUser.setIsAdmin(ArrayUtils.contains(SUPER_ADMIN_LIST, username) ? 1 : 0);
            dssUser.setLoginNum(1);
            insertUser(dssUser, workspace);
        }
    }

    /**
     * 新增保存用户信息
     *
     * @param user 用户信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertUser(DssAdminUser user, Workspace workspace) {
        tryUserOperation((ssoUserService, requestRefUser) -> {
                    SSOUserGetOperation ssoUserOperation = ssoUserService.getSSOUserGetOperation();
                    //一些没有实现该operation的如SSOAppconn
                    if (ssoUserOperation == null) {
                        return false;
                    }
                    DSSUserContentRequestRef requestRef = RequestRefUtils.getRequestRef(ssoUserOperation);
                    requestRef.setWorkspace(workspace).setUser(requestRefUser);
                    return StringUtils.isEmpty(ssoUserOperation.getUser(requestRef).getRefUserId());
                }, SSOUserService::getSSOUserCreationOperation,
                (ssoUserCreationOperation, requestRef) -> ssoUserCreationOperation.createUser(requestRef), workspace, user);
        return dssUserMapper.insertUser(user);
    }

    @Override
    public List<DssAdminUser> selectUserList(DssAdminUser user) {
        return dssUserMapper.selectUserList(user);
    }

    /**
     * 通过用户ID查询用户
     *
     * @param userId 用户ID
     * @return 用户对象信息
     */
    @Override
    public DssAdminUser selectUserById(Long userId) {
        return dssUserMapper.selectUserById(userId);
    }

    @Override
    public DssAdminUser selectUserByName(String username) {
        return dssUserMapper.selectUserByName(username);
    }


    @Override
    public int updateUser(DssAdminUser user, Workspace workspace) {
        tryUserOperation(null, SSOUserService::getSSOUserUpdateOperation,
                (ssoUserUpdateOperation, requestRef) -> ssoUserUpdateOperation.updateUser(requestRef), workspace, user);
        return dssUserMapper.updateUser(user);
    }

    @Override
    public List<String> getAllUsername() {
        return dssUserMapper.getAllUsername();
    }

    @Override
    public void deleteUser(String userName) {
        dssUserMapper.deleteUser(userName);
    }

    private <T extends SSOUserOperation> void tryUserOperation(BiPredicate<SSOUserService, DSSUserContentRequestRef.User> filter,
                                                               Function<SSOUserService, T> operationFunction,
                                                               BiFunction<T, DSSUserContentRequestRef, ResponseRef> operationConsumer,
                                                               Workspace workspace, DssAdminUser user) {
        DSSUserContentRequestRef.User requestRefUser = new DSSUserContentRequestRef.User() {
            @Override
            public String getUsername() {
                return user.getUserName();
            }

            @Override
            public String getName() {
                return user.getName();
            }

            @Override
            public Boolean isFirstLogin() {
                return user.getIsFirstLogin() == 1;
            }

            @Override
            public Boolean isAdmin() {
                return user.getIsAdmin() == 1;
            }
        };
        AppConnManager.getAppConnManager().listAppConns().forEach(appConn -> {
            if (appConn instanceof OnlySSOAppConn && CollectionUtils.isNotEmpty(appConn.getAppDesc().getAppInstances())) {
                OnlySSOAppConn onlySSOAppConn = (OnlySSOAppConn) appConn;
                appConn.getAppDesc().getAppInstances().forEach(appInstance -> {
                    SSOUserService ssoUserService = onlySSOAppConn.getOrCreateSSOStandard().getSSOUserService(appInstance);
                    if (filter != null && !filter.test(ssoUserService, requestRefUser)) {
                        return;
                    }
                    T ssoUserOperation = operationFunction.apply(ssoUserService);
                    if (ssoUserOperation == null) {
                        return;
                    }
                    DSSUserContentRequestRef requestRef = RequestRefUtils.getRequestRef(ssoUserOperation);
                    requestRef.setWorkspace(workspace).setUser(requestRefUser);
                    logger.info("try to ask AppConn {} to operate {} with user {}.", appConn.getAppDesc().getAppName(), ssoUserOperation.getClass().getSimpleName(), user);
                    ResponseRef responseRef = operationConsumer.apply(ssoUserOperation, requestRef);
                    if (responseRef.isFailed()) {
                        DSSExceptionUtils.dealWarnException(50030, responseRef.getErrorMsg(), DSSFrameworkWarnException.class);
                    }
                });
            }
        });
    }

}
