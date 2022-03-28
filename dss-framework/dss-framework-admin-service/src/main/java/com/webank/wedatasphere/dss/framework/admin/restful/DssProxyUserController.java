package com.webank.wedatasphere.dss.framework.admin.restful;

import com.webank.wedatasphere.dss.common.utils.DSSExceptionUtils;
import com.webank.wedatasphere.dss.framework.admin.common.utils.StringUtils;
import com.webank.wedatasphere.dss.framework.admin.exception.DSSAdminErrorException;
import com.webank.wedatasphere.dss.framework.admin.pojo.entity.DssProxyUser;
import com.webank.wedatasphere.dss.framework.admin.service.DssProxyUserService;
import org.apache.commons.lang.exception.ExceptionUtils;
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
import org.springframework.web.bind.annotation.RestController;
import scala.Tuple2;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;

import static com.webank.wedatasphere.dss.framework.admin.conf.ProjectConf.*;

@RequestMapping(path = "/dss/framework/admin/user", produces = {"application/json"})
@RestController
public class DssProxyUserController {
    private Boolean sslEnable = (Boolean) ServerConfiguration.BDP_SERVER_SECURITY_SSL().getValue();
    private String  PROXY_USER_TICKET_ID_STRING = ServerConfiguration.LINKIS_SERVER_SESSION_PROXY_TICKETID_KEY().getValue();
    protected final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    @Autowired
    DssProxyUserService dssProxyUserService;

    @RequestMapping(path = "proxy/list", method = RequestMethod.GET)
    public Message getProxyUserList(HttpServletRequest request) {
        String username = SecurityFilter.getLoginUsername(request);

        List<DssProxyUser> userList = dssProxyUserService.selectProxyUserList(username);
        List<String> proxyUserNameList=userList.stream().map(dssProxyUser -> dssProxyUser.getProxyUserName()).collect(Collectors.toList());
        if(DS_PROXY_SELF_ENABLE.getValue()) {
            proxyUserNameList.add(username);
        }
        return Message.ok().data("proxyUserList", proxyUserNameList);
    }

    @RequestMapping(path = "proxy/addUserCookie", method = RequestMethod.POST)
    public Message setProxyUserCookie(@RequestBody DssProxyUser userRep, HttpServletRequest req, HttpServletResponse resp) {

        String username = SecurityFilter.getLoginUsername(req);
        String trustCode = DS_TRUST_TOKEN.getValue();
        try {
            if (userRep.getUserName().equals(username)) {
                if (StringUtils.isEmpty(userRep.getUserName())) {
                    DSSExceptionUtils.dealErrorException(100101, "User name is empty", DSSAdminErrorException.class);
                } else if (StringUtils.isEmpty(userRep.getProxyUserName())) {
                    DSSExceptionUtils.dealErrorException(100102, "Proxy user name is empty", DSSAdminErrorException.class);
                } else if (dssProxyUserService.isExists(userRep.getUserName(), userRep.getProxyUserName())) {
                    for (Cookie cookie : req.getCookies()) {
                        if (null != cookie && cookie.getName().equalsIgnoreCase(PROXY_USER_TICKET_ID_STRING)) {
                            cookie.setValue(null);
                            cookie.setMaxAge(0);
                            resp.addCookie(cookie);
                        }
                    }
                }
                Tuple2<String, String> userTicketIdKv = ProxyUserSSOUtils.getProxyUserTicketKV(userRep.getProxyUserName(), trustCode);
                Cookie cookie = new Cookie(userTicketIdKv._1, userTicketIdKv._2);
                cookie.setMaxAge(-1);
                if (sslEnable){
                    cookie.setSecure(true);
                }
                cookie.setPath("/");
                resp.addCookie(cookie);

            } else {
                DSSExceptionUtils.dealErrorException(100103,"The requested user name is not a login user",DSSAdminErrorException.class);
            }
            return Message.ok("Success to add proxy user into cookie");

        } catch (Exception exception) {
            LOGGER.error("Failed to set cookie for proxy user", exception);
            return Message.error(ExceptionUtils.getRootCauseMessage(exception));
        }

    }

    @RequestMapping(path = "proxy/add", method = RequestMethod.POST)
    public Message add(@RequestBody DssProxyUser userRep, HttpServletRequest req) {
        String username = SecurityFilter.getLoginUsername(req);

        try {
            if(!username.equals(DSS_PROXY_ADMIN_NAME.getValue())){
                DSSExceptionUtils.dealErrorException(100104, "Only administrators can add proxy users", DSSAdminErrorException.class);
            }
            if(StringUtils.isEmpty(userRep.getUserName())){
                DSSExceptionUtils.dealErrorException(100105, "User name is empty", DSSAdminErrorException.class);
            }else if(StringUtils.isEmpty(userRep.getProxyUserName())){
                DSSExceptionUtils.dealErrorException(100106, "Proxy user name is empty", DSSAdminErrorException.class);
            }else  if (dssProxyUserService.isExists(userRep.getUserName(),userRep.getProxyUserName())) {
                DSSExceptionUtils.dealErrorException(100107, "Failed to add proxy user，'userName：" + userRep.getUserName() + ",proxyName："+userRep.getProxyUserName()+" already exists", DSSAdminErrorException.class);
            }
            dssProxyUserService.insertProxyUser(userRep);
            return Message.ok("Success to add proxy user");
        } catch (Exception exception) {
            LOGGER.error("Failed to add proxy user", exception);
            return Message.error(ExceptionUtils.getRootCauseMessage(exception));
        }

    }
}
