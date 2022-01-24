package com.webank.wedatasphere.dss.framework.admin.service;

import com.webank.wedatasphere.dss.framework.admin.pojo.entity.DssAdminUser;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 用户信息表 服务类
 * </p>
 *
 * @author Lvjw
 * @since 2021-06-01
 */
public interface DssAdminUserService extends IService<DssAdminUser> {

    String checkUserNameUnique(String username);

    String checkPhoneUnique(DssAdminUser user);

    String checkEmailUnique(DssAdminUser user);

    int insertUser(DssAdminUser user);

    List<DssAdminUser> selectUserList(DssAdminUser user);

    DssAdminUser selectUserById(Long userId);

    int updateUser(DssAdminUser user);

    public int resetPwd(DssAdminUser user);
}
