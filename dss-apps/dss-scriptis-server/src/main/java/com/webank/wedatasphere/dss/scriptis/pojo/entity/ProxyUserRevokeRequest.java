package com.webank.wedatasphere.dss.scriptis.pojo.entity;

import javax.validation.constraints.NotBlank;

public class ProxyUserRevokeRequest {

    @NotBlank
    private String userName;
    private String[] proxyUserNames;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String[] getProxyUserNames() {
        return proxyUserNames;
    }

    public void setProxyUserNames(String[] proxyUserNames) {
        this.proxyUserNames = proxyUserNames;
    }
}
