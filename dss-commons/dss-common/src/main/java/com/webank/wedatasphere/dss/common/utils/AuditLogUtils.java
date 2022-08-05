package com.webank.wedatasphere.dss.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuditLogUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuditLogUtils.class);

    public static void printLog(String user, String workspaceName, String msg, Object params) {
        LOGGER.info("user {} begin to do operation: {}, in workspace: {}, request params: {}", user, msg, workspaceName, params);
    }
}
