package com.webank.wedatasphere.dss.framework.admin.xml;

import com.webank.wedatasphere.dss.framework.admin.pojo.entity.DssAdminUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 用户信息表 Mapper 接口
 * </p>
 *
 * @author Lvjw
 * @since 2021-06-01
 */
@Mapper
public interface DssUserMapper extends BaseMapper<DssAdminUser> {

    /**
     * 根据条件分页查询用户列表
     *
     * @param DssAdminUser 用户信息
     * @return 用户信息集合信息
     */
    public List<DssAdminUser> selectUserList(DssAdminUser DssAdminUser);

    /**
     * 通过用户名查询用户
     *
     * @param userName 用户名
     * @return 用户对象信息
     */
    public DssAdminUser selectUserByUserName(String userName);

    /**
     * 通过用户ID查询用户
     *
     * @param id 用户ID
     * @return 用户对象信息
     */
    public DssAdminUser selectUserById(Long  id);

    /**
     * 新增用户信息
     *
     * @param user 用户信息
     * @return 结果
     */
    public int insertUser(DssAdminUser user);

    /**
     * 修改用户信息
     *
     * @param user 用户信息
     * @return 结果
     */
    public int updateUser(DssAdminUser user);


    /**
     * 重置用户密码
     *
     * @param userName 用户名
     * @param password 密码
     * @return 结果
     */
    public int resetUserPwd(@Param("userName") String userName, @Param("password") String password);

    /**
     * 通过用户ID删除用户
     *
     * @param id 用户ID
     * @return 结果
     */
    public int deleteUserById(Long id);


    /**
     * 校验用户名称是否唯一
     *
     * @param userName 用户名称
     * @return 结果
     */
    public int checkUserNameUnique(String userName);

    /**
     * 校验手机号码是否唯一
     *
     * @param phonenumber 手机号码
     * @return 结果
     */
    public DssAdminUser checkPhoneUnique(String phonenumber);

    /**
     * 校验email是否唯一
     *
     * @param email 用户邮箱
     * @return 结果
     */
    public DssAdminUser checkEmailUnique(String email);




}
