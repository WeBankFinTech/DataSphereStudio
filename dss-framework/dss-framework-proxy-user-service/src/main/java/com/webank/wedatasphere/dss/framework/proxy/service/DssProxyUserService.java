package com.webank.wedatasphere.dss.framework.proxy.service;


import com.webank.wedatasphere.dss.common.entity.DSSWorkspace;
import com.webank.wedatasphere.dss.framework.proxy.exception.DSSProxyUserErrorException;
import com.webank.wedatasphere.dss.framework.proxy.pojo.entity.DssProxyUser;
import org.apache.linkis.server.security.ProxyUserSSOUtils;
import scala.Option;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface DssProxyUserService {
    String TOKEN_PROXY_USER_HEADER_KEY="Token-Proxy-User";

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

    default String getProxyUser(HttpServletRequest request) throws DSSProxyUserErrorException {
        //header的优先级更高，如果header里指定了代理用户，则直接返回。
        String proxyUser=request.getHeader(TOKEN_PROXY_USER_HEADER_KEY);
        if(proxyUser!=null){
            return proxyUser;
        }
        Option<String> proxyUser = ProxyUserSSOUtils.getProxyUserUsername(request);
        if(proxyUser.isEmpty()) {
            throw new DSSProxyUserErrorException(60050, "proxy user is not exists in cookies.");
        }
        return proxyUser.get();
    }


}
