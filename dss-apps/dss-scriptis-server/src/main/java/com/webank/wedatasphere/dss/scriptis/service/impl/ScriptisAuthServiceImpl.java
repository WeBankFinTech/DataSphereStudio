package com.webank.wedatasphere.dss.scriptis.service.impl;

import com.webank.wedatasphere.dss.common.conf.DSSCommonConf;
import com.webank.wedatasphere.dss.common.utils.GlobalLimitsUtils;
import com.webank.wedatasphere.dss.scriptis.dao.ScriptisAuthMapper;
import com.webank.wedatasphere.dss.scriptis.pojo.entity.DssConfig;
import com.webank.wedatasphere.dss.scriptis.service.ScriptisAuthService;
import org.apache.linkis.protocol.util.ImmutablePair;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScriptisAuthServiceImpl implements ScriptisAuthService {

    public static final String USER_LIMITS_PREFIX = "wds.dss.user.limits.";
    public static final String USER_LIMITS_SUFFIX = ".user";

    @Autowired
    private ScriptisAuthMapper authMapper;

    @Override
    public Map<String, Object> getGlobalLimits(String username) {
        return GlobalLimitsUtils.getAllGlobalLimits();
    }

    public Map<String, Boolean> getUserLimits(String username, String limitName) {
        String userLimitPrefix = DSSCommonConf.GLOBAL_LIMIT_PREFIX.acquireNew() + limitName;
        List<DssConfig> userLimits = authMapper.getUserLimits(userLimitPrefix);
        return userLimits.stream().filter(dssConfig -> dssConfig.getKey().endsWith(USER_LIMITS_SUFFIX))
                .map(dssConfig -> {
                    String key = dssConfig.getKey().substring(userLimitPrefix.length(), dssConfig.getKey().length() - USER_LIMITS_SUFFIX.length());
                    boolean limit =  dssConfig.getValue().contains(username);
                    return new ImmutablePair<>(key, limit);
                })
                .collect(HashMap::new, (map, pair) -> map.put(pair.getKey(), pair.getValue()), HashMap::putAll);
    }
}
