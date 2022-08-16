package com.webank.wedatasphere.dss.common.utils;

import com.google.gson.Gson;
import com.webank.wedatasphere.dss.common.auditlog.OperateTypeEnum;
import com.webank.wedatasphere.dss.common.auditlog.TargetTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Date;

public class AuditLogUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuditLogUtils.class);

    public static void printLog(String user, String workspaceId, String workspaceName, TargetTypeEnum targetType,
                                String targetId, String targetName, OperateTypeEnum operateType,Object params) {
        String detailInfo=new Gson().toJson(params);
        LOGGER.info("[{}],[{}],[{}],[{}],[{}],[{}],[{}],[{}],[{}]",
                new Date(),user, workspaceId,workspaceName,targetType.getName(),
                targetId,targetName,operateType.getName(), detailInfo);
    }
    public static void printLog(String user, long workspaceId, String workspaceName, TargetTypeEnum targetType,
                                long targetId, String targetName, OperateTypeEnum operateType,Object params) {
        printLog(user, String.valueOf(workspaceId), workspaceName, targetType,
                String.valueOf(targetId), targetName, operateType, params);
    }
}
