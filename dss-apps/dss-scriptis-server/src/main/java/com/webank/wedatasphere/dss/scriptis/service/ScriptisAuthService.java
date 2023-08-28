package com.webank.wedatasphere.dss.scriptis.service;

import java.util.Map;

public interface ScriptisAuthService {

    Map<String,Object> getGlobalLimits(String username);

    Map<String, Boolean> getUserLimits(String username, String limitName) {
}
