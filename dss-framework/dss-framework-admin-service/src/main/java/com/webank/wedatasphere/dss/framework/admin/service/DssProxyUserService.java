package com.webank.wedatasphere.dss.framework.admin.service;


import com.webank.wedatasphere.dss.framework.admin.pojo.entity.DssProxyUser;

import java.util.List;

public interface DssProxyUserService {

    /**
     * 查询代理用户数据
     *
     * @param userName 查询代理用户的用户名
     * @return 代理用户的集合
     */
    List<DssProxyUser> selectProxyUserList(String  userName);

    int insertProxyUser(DssProxyUser dssProxyUser);

    boolean isExists(String userName,String proxyUserName);


}
