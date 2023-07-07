package com.webank.wedatasphere.dss.framework.admin.service;

import com.webank.wedatasphere.dss.framework.admin.pojo.entity.DssAdminUser;
import com.baomidou.mybatisplus.extension.service.IService;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;

import java.util.List;

public interface DssAdminUserService extends IService<DssAdminUser> {

    String checkUserNameUnique(String username);

    String checkPhoneUnique(DssAdminUser user);

    String checkEmailUnique(DssAdminUser user);

    void insertOrUpdateUser(String username, Workspace workspace);

    void insertIfNotExist(String username, Workspace workspace);

    int insertUser(DssAdminUser user, Workspace workspace);

    List<DssAdminUser> selectUserList(DssAdminUser user);

    DssAdminUser selectUserById(Long userId);

    DssAdminUser selectUserByName(String username);

    int updateUser(DssAdminUser user, Workspace workspace);

    List<String> getAllUsername();

    void deleteUser(String userName);
}
