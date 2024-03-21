package com.webank.wedatasphere.dss.framework.admin.xml;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.webank.wedatasphere.dss.framework.admin.pojo.entity.DssAdminUser;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DssUserMapper extends BaseMapper<DssAdminUser> {

    /**
     * 根据条件分页查询用户列表
     *
     * @param DssAdminUser 用户信息
     * @return 用户信息集合信息
     */
    List<DssAdminUser> selectUserList(DssAdminUser DssAdminUser);


    /**
     * 通过用户ID查询用户
     *
     * @param id 用户ID
     * @return 用户对象信息
     */
    DssAdminUser selectUserById(Long  id);

    DssAdminUser selectUserByName(String  username);

    /**
     * 新增用户信息
     *
     * @param user 用户信息
     * @return 结果
     */
    int insertUser(DssAdminUser user);

    /**
     * 修改用户信息
     *
     * @param user 用户信息
     * @return 结果
     */
    int updateUser(DssAdminUser user);



    /**
     * 校验用户名称是否唯一
     *
     * @param userName 用户名称
     * @return 结果
     */
    int checkUserNameUnique(String userName);

    /**
     * 校验手机号码是否唯一
     *
     * @param phonenumber 手机号码
     * @return 结果
     */
    DssAdminUser checkPhoneUnique(String phonenumber);

    /**
     * 校验email是否唯一
     *
     * @param email 用户邮箱
     * @return 结果
     */
    DssAdminUser checkEmailUnique(String email);


    List<String> getAllUsername();

    void deleteUser(String userName);
}
