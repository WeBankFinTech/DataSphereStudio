package com.webank.wedatasphere.dss.appconn.dolphinscheduler.sso;

import org.apache.http.client.methods.HttpGet;

import java.net.URI;

/**
 * The type Dolphin scheduler http get.
 *
 * @author yuxin.yuan
 * @date 2021/05/20
 */
public class DolphinSchedulerHttpGet extends HttpGet implements UserInfo {

    private String user;

    public DolphinSchedulerHttpGet(String url, String user) {
        super(url);
        this.user = user;
    }

    public DolphinSchedulerHttpGet(URI url, String user) {
        super(url);
        this.user = user;
    }

    @Override
    public String getUser() {
        return user;
    }

}
