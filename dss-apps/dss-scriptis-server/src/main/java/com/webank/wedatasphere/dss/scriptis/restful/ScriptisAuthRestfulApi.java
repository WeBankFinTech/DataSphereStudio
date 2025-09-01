package com.webank.wedatasphere.dss.scriptis.restful;

import com.google.common.collect.Lists;
import com.webank.wedatasphere.dss.common.conf.DSSCommonConf;
import com.webank.wedatasphere.dss.common.utils.GlobalLimitsUtils;
import com.webank.wedatasphere.dss.scriptis.config.DSSScriptisConfiguration;
import com.webank.wedatasphere.dss.scriptis.service.ScriptisAuthService;
import org.apache.linkis.common.conf.BDPConfiguration;
import org.apache.linkis.server.Message;
import org.apache.linkis.server.conf.ServerConfiguration;
import org.apache.linkis.server.security.SecurityFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    private final Logger logger = LoggerFactory.getLogger(ScriptisAuthRestfulApi.class);
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
        boolean enabled = ServerConfiguration.LINKIE_USERNAME_SUFFIX_ENABLE();
        // _cfor_f后缀 , _c 后缀
        List<String> suffixs = Lists.newArrayList(DSSCommonConf.DSS_USER_NAME_SUFFIX.getValue(),
                ServerConfiguration.LINKIE_USERNAME_SUFFIX_NAME());

        logger.info("linkis.username.suffix.enable is {}, wds.dss.username.suffix.name is {}, username is {}",
                enabled,String.join(",",suffixs),username);

        Map<String, Object> resMap = new HashMap<>(globalLimits);

        // LINKIE_USERNAME_SUFFIX_ENABLE=true,说明是联合分析后台。 如果未匹配到_cfor_f和_c结尾的用户,说明前端是联合分析环境,此时走后端配置文件。
        // 如果匹配到_cfor_f后缀则是cib数据操作间,_c后缀是普通数据操作间,此时强制写成true
        // LINKIE_USERNAME_SUFFIX_ENABLE=false 是其它集群后台,此时强制写成true
        if (enabled && suffixs.stream().noneMatch(suffix -> username.endsWith(suffix))) {
            return Message.ok().data("globalLimits", resMap);
        }

        List<String> authList = Lists.newArrayList("exportResEnable", "downloadResEnable", "resCopyEnable");
        authList.forEach(auth -> resMap.put(auth, true));

        return Message.ok().data("globalLimits", resMap);
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
