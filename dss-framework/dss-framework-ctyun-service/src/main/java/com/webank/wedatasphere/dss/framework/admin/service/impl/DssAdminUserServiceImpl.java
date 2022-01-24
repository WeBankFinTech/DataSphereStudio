package com.webank.wedatasphere.dss.framework.admin.service.impl;

import com.webank.wedatasphere.dss.framework.admin.common.constant.UserConstants;
import com.webank.wedatasphere.dss.framework.admin.common.utils.StringUtils;
import com.webank.wedatasphere.dss.framework.admin.pojo.entity.DssAdminUser;
import com.webank.wedatasphere.dss.framework.admin.xml.DssUserMapper;
import com.webank.wedatasphere.dss.framework.admin.service.DssAdminUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 用户信息表 服务实现类
 * </p>
 *
 * @author Lvjw
 * @since 2021-06-01
 */
@Service
public class DssAdminUserServiceImpl extends ServiceImpl<DssUserMapper, DssAdminUser> implements DssAdminUserService {

    @Resource
    DssUserMapper dssUserMapper;
    /**
     * 校验用户名称是否唯一
     *
     * @param userName 用户名称
     * @return 结果
     */
    @Override
    public String checkUserNameUnique(String userName)
    {
        int count = dssUserMapper.checkUserNameUnique(userName);
        if (count > 0)
        {
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
    public String checkPhoneUnique(DssAdminUser user)
    {
        Long userId = StringUtils.isNull(user.getId()) ? -1L : user.getId();
        DssAdminUser info = dssUserMapper.checkPhoneUnique(user.getPhonenumber());
        if (StringUtils.isNotNull(info) && info.getId().longValue() != userId.longValue())
        {
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
    public String checkEmailUnique(DssAdminUser user)
    {
        Long userId = StringUtils.isNull(user.getId()) ? -1L : user.getId();
        DssAdminUser info = dssUserMapper.checkEmailUnique(user.getEmail());
        if (StringUtils.isNotNull(info) && info.getId().longValue() != userId.longValue())
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }
    /**
     * 新增保存用户信息
     *
     * @param user 用户信息
     * @return 结果
     */
    @Override
    @Transactional
    public int insertUser(DssAdminUser user) {
        int rows = dssUserMapper.insertUser(user);
        return rows;
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
    public DssAdminUser selectUserById(Long userId)
    {
        return dssUserMapper.selectUserById(userId);
    }



    @Override
//    @Transactional
    public int updateUser(DssAdminUser user)
    {
        Long userId = user.getId();

        return dssUserMapper.updateUser(user);
    }

    @Override
    public int resetPwd(DssAdminUser user) {
        return dssUserMapper.updateUser(user);
    }


}
