package com.webank.wedatasphere.dss.framework.proxy.service;


import com.webank.wedatasphere.dss.common.entity.DSSWorkspace;
import com.webank.wedatasphere.dss.framework.proxy.pojo.entity.DssProxyUser;

import java.util.List;

public interface DssProxyUserService {

    /**
     * 查询代理用户数据
     *
     * @param userName 查询代理用户的用户名
     * @return 代理用户的集合
     */
    List<DssProxyUser> selectProxyUserList(String userName, DSSWorkspace workspace);

    default boolean isExists(String userName, String proxyUserName, DSSWorkspace workspace) {
        List<DssProxyUser> proxyUserList = selectProxyUserList(userName, workspace);
        if(proxyUserList == null || proxyUserList.isEmpty()) {
            return false;
        }
        return proxyUserList.stream().anyMatch(proxyUser -> proxyUser.getProxyUserName().equals(proxyUserName));
    }


}
