package com.webank.wedatasphere.dss.scriptis.service;


import com.webank.wedatasphere.dss.scriptis.pojo.entity.ScriptisProxyUser;

public interface ScriptisProxyUserService {

    int insertProxyUser(ScriptisProxyUser dssProxyUser);
    void revokeProxyUser(String userName, String[] proxyUserNames);

}
