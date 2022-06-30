package com.webank.wedatasphere.dss.framework.admin.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.webank.wedatasphere.dss.framework.admin.common.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

/**
 * <p>
 * 用户信息表
 * </p>
 */
@TableName(value = "dss_user")
public class DssAdminUser extends BaseEntity {
    private static final long serialVersionUID = 1L;
    /**
     * user_id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 昵称
     */
    private String name;


    private Integer isFirstLogin;

    /**
     * 部门id
     */
    private Long deptId;

    /**
     * 是否管理员
     */
    private Integer isAdmin;

    /**
     * 用户邮箱
     */
    private String email;

    /**
     * 手机号码
     */
    private String phonenumber;

    /**
     * 密码
     */
    private String password;

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    private String delFlag;

    private Date lastLoginTime;

    private Integer loginNum;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setIsFirstLogin(Integer isFirstLogin) {
        this.isFirstLogin = isFirstLogin;
    }

    public Integer getIsFirstLogin() {
        return isFirstLogin;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setIsAdmin(Integer isAdmin) {
        this.isAdmin = isAdmin;
    }

    public Integer getIsAdmin() {
        return isAdmin;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public Date getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public Integer getLoginNum() {
        return loginNum;
    }

    public void setLoginNum(Integer loginNum) {
        this.loginNum = loginNum;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id" , getId())
                .append("userName" , getUserName())
                .append("name" , getName())
                .append("isFirstLogin" , getIsFirstLogin())
                .append("deptId" , getDeptId())
                .append("isAdmin" , getIsAdmin())
                .append("email" , getEmail())
                .append("phonenumber" , getPhonenumber())
                .append("password" , getPassword())
                .append("delFlag" , getDelFlag())
                .append("createTime" , getCreateTime())
                .append("updateTime" , getUpdateTime())
                .append("remark" , getRemark())
                .append("lastLoginTime", lastLoginTime)
                .append("loginNum" , loginNum)
                .toString();
    }

    private DssAdminDept dept;

    public DssAdminDept getDept() {
        return dept;
    }

    public void setDept(DssAdminDept dept) {
        this.dept = dept;
    }

}
