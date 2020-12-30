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
    private List<String> hdfsList;

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

    public List<String> getUserPath(){
//        hdfsList.add("")
        return hdfsList;
    }


}
