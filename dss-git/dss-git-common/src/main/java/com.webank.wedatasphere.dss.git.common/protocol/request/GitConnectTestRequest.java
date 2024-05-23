package com.webank.wedatasphere.dss.git.common.protocol.request;

public class GitConnectTestRequest {
    private String username;
    private String token;
    private String password;
    private String type;

    public GitConnectTestRequest(String username, String token, String password, String type) {
        this.username = username;
        this.token = token;
        this.password = password;
        this.type = type;
    }

    public GitConnectTestRequest() {
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
