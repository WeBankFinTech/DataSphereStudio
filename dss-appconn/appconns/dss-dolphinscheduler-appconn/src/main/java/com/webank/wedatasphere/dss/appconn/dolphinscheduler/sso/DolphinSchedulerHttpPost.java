package com.webank.wedatasphere.dss.appconn.dolphinscheduler.sso;

import org.apache.http.client.methods.HttpPost;

public class DolphinSchedulerHttpPost extends HttpPost implements UserInfo {

    private String user;

    public DolphinSchedulerHttpPost(String url, String user) {
        super(url);
        this.user = user;
    }

    @Override
    public String getUser() {
        return user;
    }

}
