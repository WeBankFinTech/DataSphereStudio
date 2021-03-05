package com.webank.wedatasphpere.dss.user.dto.request;

public class LinuxServer {

    private String linuxHost; //master ip，Comma separated
    private String linuxLoginUser;
    private String linuxLoginPassword;


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
