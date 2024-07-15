package com.webank.wedatasphere.dss.scriptis.service.impl;

import com.webank.wedatasphere.dss.common.utils.GlobalLimitsUtils;
import com.webank.wedatasphere.dss.scriptis.dao.ScriptisAuthMapper;
import com.webank.wedatasphere.dss.scriptis.pojo.entity.DssUserLimit;
import com.webank.wedatasphere.dss.scriptis.service.ScriptisAuthService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScriptisAuthServiceImpl implements ScriptisAuthService {

    @Autowired
    private ScriptisAuthMapper authMapper;

    @Override
    public Map<String, Object> getGlobalLimits(String username) {
        return GlobalLimitsUtils.getAllGlobalLimits(username);
    }

    public Map<String, Object> getUserLimits(String username, String limitName) {
        List<DssUserLimit> userLimits = authMapper.getUserLimits(limitName);
        Map<String, Object> res = new HashMap<>();
        userLimits.forEach(dssUserLimit -> {
            String key = dssUserLimit.getLimitName();
            Object value = StringUtils.isNumeric(dssUserLimit.getValue()) ? Integer.parseInt(dssUserLimit.getValue()) : dssUserLimit.getValue();
            Object retVal = StringUtils.contains(dssUserLimit.getUserName(),username) ? value : null;
            res.put(key,retVal);
        });
        return res;
    }
}
