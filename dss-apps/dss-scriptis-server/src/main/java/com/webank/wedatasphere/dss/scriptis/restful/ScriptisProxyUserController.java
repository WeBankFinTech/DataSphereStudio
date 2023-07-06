package com.webank.wedatasphere.dss.scriptis.restful;

import com.webank.wedatasphere.dss.common.auditlog.OperateTypeEnum;
import com.webank.wedatasphere.dss.common.auditlog.TargetTypeEnum;
import com.webank.wedatasphere.dss.common.conf.DSSCommonConf;
import com.webank.wedatasphere.dss.common.utils.AuditLogUtils;
import com.webank.wedatasphere.dss.framework.proxy.restful.DssProxyUserController;
import com.webank.wedatasphere.dss.scriptis.pojo.entity.ProxyUserRevokeRequest;
import com.webank.wedatasphere.dss.scriptis.pojo.entity.ScriptisProxyUser;
import com.webank.wedatasphere.dss.scriptis.service.ScriptisProxyUserService;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.linkis.server.Message;
import org.apache.linkis.server.security.SecurityFilter;
import org.apache.linkis.server.utils.ModuleUserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

import static com.webank.wedatasphere.dss.framework.common.conf.TokenConf.HPMS_USER_TOKEN;

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
    @PostMapping("/revokeProxyUser")
    public Message revokeProxyUser(HttpServletRequest httpServletRequest,
                                   @Validated @RequestBody ProxyUserRevokeRequest proxyUserRevokeRequest){
        String userName = proxyUserRevokeRequest.getUserName();
        String[] proxyUserNames = proxyUserRevokeRequest.getProxyUserNames();
        String token = ModuleUserUtils.getToken(httpServletRequest);
        if (StringUtils.isNotBlank(token)) {
            if(!token.equals(HPMS_USER_TOKEN)){
                return Message.error("Token:" + token + " has no permission to revoke proxyUser.");
            }
        }else {
            return Message.error("User:" + userName + " has no permission to revoke proxyUser.");
        }
        scriptisProxyUserService.revokeProxyUser(userName,proxyUserNames);
        AuditLogUtils.printLog(userName,null, null, TargetTypeEnum.WORKSPACE_ROLE,null,
                "deleteProxyUser", OperateTypeEnum.DELETE,"userName:" + userName + " ,proxyUserNames:" + Arrays.toString(proxyUserNames));
        return Message.ok();
    }

}
