package com.webank.wedatasphpere.dss.user.dto.request;

import java.util.HashMap;
import java.util.List;

/**
 * @program: user-manager
 * @description: 施工单数据结构
 * @author: luxl@chinatelecom.cn
 * @create: 2020-08-12 14:29
 **/
public class AuthorizationBody {

    private String username;
    private String password;
    private String dssInstallDir;
    private String azkakanDir;

    public String getAzkakanDir() {
        return azkakanDir;
    }

    public void setAzkakanDir(String azkakanDir) {
        this.azkakanDir = azkakanDir;
    }

    public String getDssInstallDir() {
        return dssInstallDir;
    }

    public void setDssInstallDir(String installDir) {
        this.dssInstallDir = installDir;
    }

    public List<HashMap<String, String>> getPaths() {
        return paths;
    }

    public void setPaths(List<HashMap<String, String>> paths) {
        this.paths = paths;
    }

    private List<HashMap<String,String>> paths;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDatabaseName(){
        return this.username + "_default";
    }



}
