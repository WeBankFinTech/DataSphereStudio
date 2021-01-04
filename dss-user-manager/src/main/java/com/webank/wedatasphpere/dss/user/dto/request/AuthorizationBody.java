package com.webank.wedatasphpere.dss.user.dto.request;


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

    public List<String> getPaths() {
        return paths;
    }

    public void setPaths(List<String> paths) {
        this.paths = paths;
    }

    private List<String> paths;

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
