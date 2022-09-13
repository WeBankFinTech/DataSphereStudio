package com.webank.wedatasphere.dss.framework.proxy.pojo.entity;


public class DssProxyUserImpl implements DssProxyUser {

    private String userName;
    private String proxyUserName;

    public DssProxyUserImpl() {
    }

    public DssProxyUserImpl(String userName, String proxyUserName) {
        this.userName = userName;
        this.proxyUserName = proxyUserName;
    }

    @Override
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String getProxyUserName() {
        return proxyUserName;
    }

    public void setProxyUserName(String proxyUserName) {
        this.proxyUserName = proxyUserName;
    }
}
