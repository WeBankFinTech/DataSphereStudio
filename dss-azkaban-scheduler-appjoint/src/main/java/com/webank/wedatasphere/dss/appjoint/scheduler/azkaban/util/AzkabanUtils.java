package com.webank.wedatasphere.dss.appjoint.scheduler.azkaban.util;

import com.google.gson.Gson;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.util.Map;

/**
 * Created by cooperyang on 2019/9/18.
 */
public class AzkabanUtils {
    public static String handleAzkabanEntity(String entityString) throws IOException {
        Gson gson = new Gson();
        Object object = gson.fromJson(entityString, Object.class);
        String status = null;
        String message = null;
        if (object instanceof Map) {
            Map map = (Map) object;
            if (map.get("status") != null) {
                status = map.get("status").toString();
            }
            if (StringUtils.isNotEmpty(status)) {
                if (null != map.get("message")) {
                    message = map.get("message").toString();
                }
            }
            if ("error".equalsIgnoreCase(status)) {
                return message;
            }
        }
        return "success";
    }
}
