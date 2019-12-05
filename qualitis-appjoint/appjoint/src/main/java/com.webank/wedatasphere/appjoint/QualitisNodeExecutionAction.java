package com.webank.wedatasphere.appjoint;

import com.webank.wedatasphere.dss.appjoint.execution.common.NodeExecutionAction;

/**
 * @author howeye
 */
public class QualitisNodeExecutionAction implements NodeExecutionAction {

    private String applicationId;

    public QualitisNodeExecutionAction(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getApplicationId() {
        return applicationId;
    }
}
