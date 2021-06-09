package com.webank.wedatasphere.dss.framework.admin.service;

import com.webank.wedatasphere.dss.framework.admin.pojo.entity.DssUser;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户信息表 服务类
 * </p>
 *
 * @author Lvjw
 * @since 2021-06-01
 */
public interface DssAdminUserService extends IService<DssUser> {

    String checkUserNameUnique(String username);

    String checkPhoneUnique(DssUser user);

    String checkEmailUnique(DssUser user);

    int insertUser(DssUser user);

    List<DssUser> selectUserList(DssUser user);

    DssUser selectUserById(Long userId);

    int updateUser(DssUser user);
}
