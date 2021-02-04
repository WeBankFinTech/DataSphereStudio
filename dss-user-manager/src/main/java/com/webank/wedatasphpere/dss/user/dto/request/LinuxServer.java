package com.webank.wedatasphpere.dss.user.dto.request;

public class LinuxServer {

    private String linuxHost; //服务器ip,用逗号分隔
    private String linuxLoginUser; //服务器用户名
    private String linuxLoginPassword; //服务器密码


    public String getLinuxHost() {
        return linuxHost;
    }

    public void setLinuxHost(String linuxHosts) {
        this.linuxHost = linuxHosts;
    }

    public String getLinuxLoginUser() {
        return linuxLoginUser;
    }

    public void setLinuxLoginUser(String linuxLoginUser) {
        this.linuxLoginUser = linuxLoginUser;
    }

    public String getLinuxLoginPassword() {
        return linuxLoginPassword;
    }

    public void setLinuxLoginPassword(String linuxLoginPassword) {
        this.linuxLoginPassword = linuxLoginPassword;
    }


}
