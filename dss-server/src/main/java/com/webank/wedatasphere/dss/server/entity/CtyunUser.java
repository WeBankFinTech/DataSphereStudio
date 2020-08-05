package com.webank.wedatasphere.dss.server.entity;

import java.util.Date;

/**
 * The type Ctyun user.
 *
 * @author yuxin.yuan
 * @date 2020/07/29
 */
public class CtyunUser {

    private String id;

    private String username;

    private String name;

    private String ctyunUsername;

    private String password;

    private Date expireTime;

    private String workOrderItemConfig;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCtyunUsername() {
        return ctyunUsername;
    }

    public void setCtyunUsername(String ctyunUsername) {
        this.ctyunUsername = ctyunUsername;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }

    public String getWorkOrderItemConfig() {
        return workOrderItemConfig;
    }

    public void setWorkOrderItemConfig(String workOrderItemConfig) {
        this.workOrderItemConfig = workOrderItemConfig;
    }
}
