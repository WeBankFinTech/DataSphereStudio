package com.webank.wedatasphere.dss.scriptis.restful;

import com.webank.wedatasphere.dss.common.conf.DSSCommonConf;
import com.webank.wedatasphere.dss.common.utils.GlobalLimitsUtils;
import org.apache.linkis.common.conf.BDPConfiguration;
import org.apache.linkis.server.Message;
import org.apache.linkis.server.conf.ServerConfiguration;
import org.apache.linkis.server.security.SecurityFilter;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import java.util.HashMap;
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

    @PostConstruct
    public void init() {
        BDPConfiguration.set(DSSCommonConf.ALL_GLOBAL_LIMITS_PREFIX.key(), GLOBAL_LIMITS_PREFIX);
        BDPConfiguration.set(DSSCommonConf.GLOBAL_LIMIT_PREFIX.key(), GLOBAL_LIMIT_PREFIX);
    }

    @RequestMapping(value = "/globalLimits", method = RequestMethod.GET)
    public Message globalLimits(HttpServletRequest req) {
        String username = SecurityFilter.getLoginUsername(req);
        Map globalLimits = GlobalLimitsUtils.getAllGlobalLimits();
        Map resMap = new HashMap(globalLimits);
        //临时代码，兼容特定环境下不同用户需要不同的权限
        if (username.endsWith(ServerConfiguration.LINKIE_USERNAME_SUFFIX_NAME())) {
            resMap.put("resCopyEnable", true);
            resMap.put("resultSetExportEnable", true);
            resMap.put("downloadResEnable", true);
        }
        return Message.ok().data("globalLimits", resMap);
    }

    @RequestMapping(value = "/globalLimits/{globalLimitName}",method = RequestMethod.GET)
    public Message globalLimit(@PathVariable("globalLimitName") String globalLimitName) {
        return Message.ok().data("globalLimitName", globalLimitName)
                .data("content", GlobalLimitsUtils.getGlobalLimitMap(globalLimitName));
    }

}
