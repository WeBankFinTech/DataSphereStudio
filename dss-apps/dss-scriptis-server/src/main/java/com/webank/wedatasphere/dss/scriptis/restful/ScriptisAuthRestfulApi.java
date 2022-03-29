package com.webank.wedatasphere.dss.scriptis.restful;

import org.apache.commons.lang.StringUtils;
import org.apache.linkis.common.conf.BDPConfiguration;
import org.apache.linkis.protocol.util.ImmutablePair;
import org.apache.linkis.server.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

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
    private Map<String, Object> globalLimits;
    private final Map<String, Map<String, Object>> globalLimitContents = new HashMap<>();

    @RequestMapping(value = "/globalLimits",method = RequestMethod.GET)
    public Message globalLimits() {
        if(globalLimits == null) {
            synchronized (ScriptisAuthRestfulApi.class) {
                if(globalLimits == null) {
                    globalLimits = getMap(GLOBAL_LIMITS_PREFIX);
                    logger.info("loaded Scriptis global limits is {}.", globalLimits);
                }
            }
        }
        return Message.ok().data("globalLimits", globalLimits);
    }

    @RequestMapping(value = "/globalLimits/{globalLimitName}",method = RequestMethod.GET)
    public Message globalLimit(@PathVariable("globalLimitName") String globalLimitName) {
        Supplier<Message> function = () -> Message.ok().data("globalLimitName", globalLimitName).data("content", globalLimitContents.get(globalLimitName));
        if(globalLimitContents.containsKey(globalLimitName)) {
            return function.get();
        }
        synchronized (globalLimitContents) {
            if(!globalLimitContents.containsKey(globalLimitName)) {
                Map<String, Object> globalLimitContent = getMap(GLOBAL_LIMIT_PREFIX + globalLimitName + ".");
                logger.info("loaded Scriptis global limit {} content are {}.", globalLimitName, globalLimitContent);
                globalLimitContents.put(globalLimitName, globalLimitContent);
            }
        }
        return function.get();
    }

    private Map<String, Object> getMap(String prefix) {
        return BDPConfiguration.properties().entrySet().stream()
                .filter(entry -> entry.getKey().toString().startsWith(prefix) && entry.getValue() != null && StringUtils.isNotBlank(entry.getValue().toString()))
                .map(entry -> {
                    String key = ((String) entry.getKey()).substring(prefix.length());
                    if("true".equals(entry.getValue()) || "false".equals(entry.getValue())) {
                        return new ImmutablePair<>(key, Boolean.parseBoolean((String) entry.getValue()));
                    } else {
                        return new ImmutablePair<>(key, entry.getValue());
                    }
                }).collect(HashMap::new, (map, pair) -> map.put(pair.getKey(), pair.getValue()),
                        HashMap::putAll);
    }
}
