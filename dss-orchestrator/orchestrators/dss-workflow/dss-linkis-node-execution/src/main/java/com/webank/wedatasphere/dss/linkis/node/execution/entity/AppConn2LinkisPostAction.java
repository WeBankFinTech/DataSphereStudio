package com.webank.wedatasphere.dss.linkis.node.execution.entity;

import com.webank.wedatasphere.dss.linkis.node.execution.utils.LinkisJobExecutionUtils;
import org.apache.linkis.httpclient.request.POSTAction;
import org.apache.linkis.httpclient.request.UserAction;

public class AppConn2LinkisPostAction extends POSTAction implements UserAction {

    private String url;
    private String user;

    @Override
    public String getRequestPayload() {
        return LinkisJobExecutionUtils.gson.toJson(getRequestPayloads());
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String getURL() {
        return url;
    }

    @Override
    public void setUser(String user) {
        this.user = user;
    }

    @Override
    public String getUser() {
        return user;
    }
}
