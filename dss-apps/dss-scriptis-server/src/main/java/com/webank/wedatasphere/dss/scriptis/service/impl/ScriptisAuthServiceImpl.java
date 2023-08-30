package com.webank.wedatasphere.dss.scriptis.service.impl;

import com.webank.wedatasphere.dss.common.utils.GlobalLimitsUtils;
import com.webank.wedatasphere.dss.scriptis.dao.ScriptisAuthMapper;
import com.webank.wedatasphere.dss.scriptis.pojo.entity.DssConfig;
import com.webank.wedatasphere.dss.scriptis.service.ScriptisAuthService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScriptisAuthServiceImpl implements ScriptisAuthService {

    public static final String USER_LIMITS_PREFIX = "wds.dss.user.limits.";
    public static final String DOWNLOAD_COUNT = "downloadCount";

    @Autowired
    private ScriptisAuthMapper authMapper;

    @Override
    public Map<String, Object> getGlobalLimits(String username) {
        return GlobalLimitsUtils.getAllGlobalLimits();
    }

    public Map<String, Object> getUserLimits(String username, String limitName) {
        String userLimitPrefix = StringUtils.join(USER_LIMITS_PREFIX, limitName);
        List<DssConfig> userLimits = authMapper.getUserLimits(userLimitPrefix);
        Map<String, Object> res = new HashMap<>();
        userLimits.forEach(dssConfig -> {
            String key = dssConfig.getKey().substring(userLimitPrefix.length());
            if (DOWNLOAD_COUNT.equals(key)) {
                Integer limitCount = StringUtils.contains(dssConfig.getCondition(),username) ? Integer.parseInt(dssConfig.getValue()) : -1;
                res.put(key,limitCount);
            }else {
                res.put(key,dssConfig.getValue());
            }

        });
        return res;
    }
}
