package com.webank.wedatasphere.dss.scriptis.restful;

import com.webank.wedatasphere.dss.common.conf.DSSCommonConf;
import com.webank.wedatasphere.dss.framework.proxy.restful.DssProxyUserController;
import com.webank.wedatasphere.dss.scriptis.pojo.entity.ScriptisProxyUser;
import com.webank.wedatasphere.dss.scriptis.service.ScriptisProxyUserService;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.linkis.server.Message;
import org.apache.linkis.server.security.SecurityFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RequestMapping(path = "/dss/scriptis/proxy", produces = {"application/json"})
@RestController
public class ScriptisProxyUserController extends DssProxyUserController {

    @Autowired
    private ScriptisProxyUserService scriptisProxyUserService;

    @RequestMapping(path = "add", method = RequestMethod.POST)
    public Message add(@RequestBody ScriptisProxyUser userRep, HttpServletRequest req) {
        String username = SecurityFilter.getLoginUsername(req);
        if(!ArrayUtils.contains(DSSCommonConf.SUPER_ADMIN_LIST, username)){
            return Message.error("Only super admin can add proxy users.");
        } else if(StringUtils.isEmpty(userRep.getUserName())){
            return Message.error("userName is null.");
        } else if(StringUtils.isEmpty(userRep.getProxyUserName())){
            return Message.error("proxyUser is null.");
        } else if (dssProxyUserService.isExists(userRep.getUserName(), userRep.getProxyUserName(), null)) {
            return Message.error("Failed to add proxy user，'userName：" + userRep.getUserName() + ", proxyName："+userRep.getProxyUserName()+" already exists.");
        }
        LOGGER.info("admin {} try to add proxy user, params:{}.", username, userRep);
        try {
            scriptisProxyUserService.insertProxyUser(userRep);
        } catch (Exception exception) {
            LOGGER.error("Failed to add proxy user.", exception);
            return Message.error(ExceptionUtils.getRootCauseMessage(exception));
        }
        return Message.ok("Success to add proxy user.");
    }

}
