package com.webank.wedatasphere.dss.git.common.protocol.request;

public class GitConnectRequest {
    private String username;
    private String token;

    public GitConnectRequest(String username, String token) {
        this.username = username;
        this.token = token;
    }

    public GitConnectRequest() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
