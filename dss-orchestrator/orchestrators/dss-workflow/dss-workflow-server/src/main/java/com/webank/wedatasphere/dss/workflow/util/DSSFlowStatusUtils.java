package com.webank.wedatasphere.dss.workflow.util;

import com.webank.wedatasphere.dss.workflow.dao.LockMapper;
import org.apache.commons.lang.StringUtils;

public class DSSFlowStatusUtils {
    private static LockMapper lockMapper;

    public static void updateFlowStatus(Long flowId, String status) {
        String oldStatus = lockMapper.selectStatusByFlowId(flowId);
        if (StringUtils.isEmpty(oldStatus)) {
            lockMapper.insertFlowStatus(flowId, status);
        } else {
            lockMapper.updateFlowStatus(flowId, status);
        }
    }
}
