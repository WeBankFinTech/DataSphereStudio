package com.webank.wedatasphere.dss.scriptis.service.impl;

import com.webank.wedatasphere.dss.common.utils.GlobalLimitsUtils;
import com.webank.wedatasphere.dss.scriptis.service.ScriptisAuthService;

import java.util.Map;

public class ScriptisAuthServiceImpl implements ScriptisAuthService {

    @Override
    public Map<String, Object> getGlobalLimits(String username) {
        return GlobalLimitsUtils.getAllGlobalLimits();
    }
}
