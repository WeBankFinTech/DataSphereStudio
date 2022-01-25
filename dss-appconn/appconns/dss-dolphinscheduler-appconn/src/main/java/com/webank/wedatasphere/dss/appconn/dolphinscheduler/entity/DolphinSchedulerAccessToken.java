package com.webank.wedatasphere.dss.appconn.dolphinscheduler.entity;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * The type Dolphin scheduler access token.
 *
 * @author yuxin.yuan
 * @date 2021/06/09
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DolphinSchedulerAccessToken {

    private int id;

    private int userId;

    private String token;

    private Date expireTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Date getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }
}
