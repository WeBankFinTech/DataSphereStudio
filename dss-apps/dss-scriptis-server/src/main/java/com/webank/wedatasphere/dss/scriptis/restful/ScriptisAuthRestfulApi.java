package com.webank.wedatasphere.dss.scriptis.restful;

import com.webank.wedatasphere.dss.common.conf.DSSCommonConf;
import com.webank.wedatasphere.dss.common.utils.GlobalLimitsUtils;
import com.webank.wedatasphere.dss.scriptis.config.DSSScriptisConfiguration;
import com.webank.wedatasphere.dss.scriptis.service.ScriptisAuthService;
import org.apache.commons.lang.StringUtils;
import org.apache.linkis.common.conf.BDPConfiguration;
import org.apache.linkis.server.Message;
import org.apache.linkis.server.security.SecurityFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

import static com.webank.wedatasphere.dss.scriptis.config.DSSScriptisConfiguration.GLOBAL_LIMITS_PREFIX;
import static com.webank.wedatasphere.dss.scriptis.config.DSSScriptisConfiguration.GLOBAL_LIMIT_PREFIX;

/**
 * @author enjoyyin
 * @date 2022-03-29
 * @since 0.5.0
 */
@RequestMapping(path = "/dss/scriptis", produces = {"application/json"})
@RestController
public class ScriptisAuthRestfulApi {

    @Autowired
    private ScriptisAuthService scriptisAuthService;

    @PostConstruct
    public void init() {
        BDPConfiguration.set(DSSCommonConf.ALL_GLOBAL_LIMITS_PREFIX.key(), GLOBAL_LIMITS_PREFIX);
        BDPConfiguration.set(DSSCommonConf.GLOBAL_LIMIT_PREFIX.key(), GLOBAL_LIMIT_PREFIX);
    }

    @RequestMapping(value = "/globalLimits", method = RequestMethod.GET)
    public Message globalLimits(HttpServletRequest req) {
        String username = SecurityFilter.getLoginUsername(req);
        Map<String,Object> globalLimits = scriptisAuthService.getGlobalLimits(username);
        if (DSSScriptisConfiguration.isInCopilotWhiteList(username)) {
            globalLimits.put(DSSScriptisConfiguration.COPILOT_ENABLE_KEY, true);
        }
        return Message.ok().data("globalLimits", globalLimits);
    }

    @RequestMapping(value = "/globalLimits/{globalLimitName}",method = RequestMethod.GET)
    public Message globalLimit(@PathVariable("globalLimitName") String globalLimitName) {
        return Message.ok().data("globalLimitName", globalLimitName)
                .data("content", GlobalLimitsUtils.getGlobalLimitMap(globalLimitName));
    }

    @RequestMapping(value = "/userLimits", method = RequestMethod.GET)
    public Message userLimit(HttpServletRequest req, @RequestParam(value = "limitName",required = false) String limitName) {
        String username = SecurityFilter.getLoginUsername(req);
        return Message.ok()
                .data("userLimits", scriptisAuthService.getUserLimits(username, limitName));
    }

}
