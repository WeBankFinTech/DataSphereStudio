package com.webank.wedatasphere.dss.appconn.dolphinscheduler.sso;

import java.net.URI;

import org.apache.http.client.methods.HttpPost;

public class DolphinSchedulerHttpPost extends HttpPost implements UserInfo {

    private String user;

    public DolphinSchedulerHttpPost(String url, String user) {
        super(url);
        this.user = user;
    }

    public DolphinSchedulerHttpPost(URI url, String user) {
        super(url);
        this.user = user;
    }

    @Override
    public String getUser() {
        return user;
    }

}
