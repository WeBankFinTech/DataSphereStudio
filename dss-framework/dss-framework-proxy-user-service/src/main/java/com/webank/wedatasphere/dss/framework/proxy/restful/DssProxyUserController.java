package com.webank.wedatasphere.dss.framework.proxy.restful;

import com.webank.wedatasphere.dss.common.utils.DSSExceptionUtils;
import com.webank.wedatasphere.dss.common.utils.DomainUtils;
import com.webank.wedatasphere.dss.common.utils.ScalaFunctionAdapter;
import com.webank.wedatasphere.dss.framework.proxy.conf.ProxyUserConfiguration;
import com.webank.wedatasphere.dss.framework.proxy.pojo.entity.DssProxyUser;
import com.webank.wedatasphere.dss.framework.proxy.pojo.entity.DssProxyUserImpl;
import com.webank.wedatasphere.dss.framework.proxy.service.DssProxyUserService;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.standard.common.exception.AppStandardWarnException;
import com.webank.wedatasphere.dss.standard.sso.utils.SSOHelper;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.linkis.server.Message;
import org.apache.linkis.server.conf.ServerConfiguration;
import org.apache.linkis.server.security.ProxyUserSSOUtils;
import org.apache.linkis.server.security.SecurityFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import scala.Tuple2;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;

import static com.webank.wedatasphere.dss.framework.proxy.conf.ProxyUserConfiguration.DS_PROXY_SELF_ENABLE;
import static com.webank.wedatasphere.dss.framework.proxy.conf.ProxyUserConfiguration.DS_TRUST_TOKEN;


public class DssProxyUserController {

    private Boolean sslEnable = (Boolean) ServerConfiguration.BDP_SERVER_SECURITY_SSL().getValue();
    protected final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Autowired
    protected DssProxyUserService dssProxyUserService;

    @RequestMapping(path = "list", method = RequestMethod.GET)
    public Message getProxyUserList(HttpServletRequest request) {
        if(!ProxyUserConfiguration.isProxyUserEnable()) {
            return Message.error("proxy user service is not enable, please ask admin for help.");
        }
        String username = SecurityFilter.getLoginUsername(request);
        Workspace workspace = new Workspace();
        try {
            workspace = SSOHelper.getWorkspace(request);
        } catch (AppStandardWarnException ignored) {}
        Workspace finalWorkspace = workspace;
        return DSSExceptionUtils.applyMessage(() -> dssProxyUserService.selectProxyUserList(username, finalWorkspace),
            userList -> {
               LOGGER.info("user {} got proxy list. userList: {}.", username, userList);
               List<String> proxyUserNameList = userList.stream().map(DssProxyUser::getProxyUserName).collect(Collectors.toList());
               if(DS_PROXY_SELF_ENABLE.getValue()&&!proxyUserNameList.contains(username)) {
                   proxyUserNameList.add(username);
               }
               return Message.ok().data("proxyUserList", proxyUserNameList);
           }, "fetch proxy user list failed.");
    }

    @RequestMapping(path = "getProxyUser", method = RequestMethod.GET)
    public Message getProxyUser(HttpServletRequest request) {
        if(!ProxyUserConfiguration.isProxyUserEnable()) {
            return Message.error("proxy user service is not enable, please ask admin for help.");
        }
        String username = SecurityFilter.getLoginUsername(request);
        LOGGER.info("user {} try to get proxy user.", username);
        return DSSExceptionUtils.getMessage(() -> dssProxyUserService.getProxyUser(request),
                proxyUser -> Message.ok().data("userName", username).data("proxyUser", proxyUser), "");
    }

    @RequestMapping(path = "setProxyUser", method = RequestMethod.POST)
    public Message setProxyUserCookie(@RequestBody DssProxyUserImpl userRep,
                                      HttpServletRequest req,
                                      HttpServletResponse resp) {
        if(!ProxyUserConfiguration.isProxyUserEnable()) {
            return Message.error("proxy user service is not enable, please ask admin for help.");
        }
        String username = SecurityFilter.getLoginUsername(req);
        if (StringUtils.isEmpty(userRep.getUserName())) {
            return Message.error("userName is null.");
        } else if (StringUtils.isEmpty(userRep.getProxyUserName())) {
            return Message.error("proxyUserName is null.");
        } else if(!username.equals(userRep.getUserName()) && !username.equals(userRep.getProxyUserName())) {
            return Message.error("The requested user name is not a login user.");
        }
        LOGGER.info("user {} try to add user proxy cookie, params:{}.", username, userRep);
        // 兼容linkis端直接为登陆用户名加_c后缀的场景
        if (userRep.getProxyUserName().equals(username)) {
            userRep.setUserName(userRep.getProxyUserName());
        }
        Workspace workspace = new Workspace();
        try {
            workspace = SSOHelper.getWorkspace(req);
        } catch (AppStandardWarnException ignored) {} // ignore error
        try {
            if ((ProxyUserConfiguration.DS_PROXY_SELF_ENABLE.getValue() && userRep.getUserName().equalsIgnoreCase(userRep.getProxyUserName())) ||
                    dssProxyUserService.isExists(userRep.getUserName(), userRep.getProxyUserName(), workspace)) {
                ProxyUserSSOUtils.removeProxyUser(ScalaFunctionAdapter.doSupplier(req::getCookies));
            } else {
                LOGGER.info("user {} have no permission to proxy to user {}.", userRep.getUserName(), userRep.getProxyUserName());
                return Message.error("user " + userRep.getUserName() + " have no permission to proxy to user " + userRep.getProxyUserName());
            }
        } catch (Exception exception) {
            LOGGER.error("Failed to set proxy user to cookie.", exception);
            return Message.error(ExceptionUtils.getRootCauseMessage(exception));
        }
        Tuple2<String, String> userTicketIdKv = ProxyUserSSOUtils.getProxyUserTicketKV(userRep.getProxyUserName(), DS_TRUST_TOKEN.getValue());
        Cookie cookie = new Cookie(userTicketIdKv._1, userTicketIdKv._2);
        cookie.setMaxAge(-1);
        if (sslEnable){
            cookie.setSecure(true);
        }
        cookie.setDomain(DomainUtils.getCookieDomain(req));
        cookie.setPath("/");
        resp.addCookie(cookie);
        return Message.ok("Success to add proxy user into cookie.");
    }

}
