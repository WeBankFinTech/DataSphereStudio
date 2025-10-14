package com.webank.wedatasphere.dss.workflow.common.protocol;


import javax.servlet.http.Cookie;

public class RequestLockWorkflow {
    public RequestLockWorkflow() {
    }

    private String username;
    private Cookie[] cookies;
    private Long flowId;

    public RequestLockWorkflow(String username, Cookie[] cookies, Long flowId) {
        this.username = username;
        this.cookies = cookies;
        this.flowId = flowId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Cookie[] getCookies() {
        return cookies;
    }

    public void setCookies(Cookie[] cookies) {
        this.cookies = cookies;
    }

    public Long getFlowId() {
        return flowId;
    }

    public void setFlowId(Long flowId) {
        this.flowId = flowId;
    }
}
