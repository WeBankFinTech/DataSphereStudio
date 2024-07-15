package com.webank.wedatasphere.dss.common.utils;

import com.webank.wedatasphere.dss.common.conf.DSSCommonConf;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.linkis.common.conf.BDPConfiguration;
import org.apache.linkis.protocol.util.ImmutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author enjoyyin
 * @date 2022-09-21
 * @since 0.5.0
 */
public class GlobalLimitsUtils {

    private static final Logger logger = LoggerFactory.getLogger(GlobalLimitsUtils.class);

    private static volatile Map<String, Object> globalLimits;
    private static final Map<String, Map<String, Object>> globalLimitContents = new HashMap<>();

    public static Map<String, Object> getAllGlobalLimits() {
        if(globalLimits == null) {
            synchronized (GlobalLimitsUtils.class) {
                if(globalLimits == null) {
                    globalLimits = MapUtils.unmodifiableMap(getMap(DSSCommonConf.ALL_GLOBAL_LIMITS_PREFIX.acquireNew()));
                    logger.info("loaded global limits is {}.", globalLimits);
                }
            }
        }
        return globalLimits;
    }

    public static Map<String, Object> getGlobalLimitMap(String globalLimitName) {
        if(globalLimitContents.containsKey(globalLimitName)) {
            return globalLimitContents.get(globalLimitName);
        }
        synchronized (globalLimitContents) {
            if(!globalLimitContents.containsKey(globalLimitName)) {
                Map<String, Object> globalLimitContent = getMap(DSSCommonConf.GLOBAL_LIMIT_PREFIX.acquireNew() + globalLimitName + ".");
                logger.info("loaded global limit {}, the contents are {}.", globalLimitName, globalLimitContent);
                globalLimitContents.put(globalLimitName, MapUtils.unmodifiableMap(globalLimitContent));
            }
        }
        return globalLimitContents.get(globalLimitName);
    }

    private static Map<String, Object> getMap(String prefix) {
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
