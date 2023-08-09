package com.webank.wedatasphere.dss.standard.app.sso.plugin.filter.proxy;

import javax.servlet.http.HttpServletRequest;

/**
 * @author enjoyyin
 * @date 2022-09-02
 * @since 0.5.0
 */
public interface HttpSessionProxyUserInterceptor extends ProxyUserInterceptor {

    void addUserToSession(String user, String proxyUser, HttpServletRequest req);

    @Override
    default ProxyUserType getProxyUserType() {
        return ProxyUserType.USER_WITH_PROXY_USER;
    }
}
