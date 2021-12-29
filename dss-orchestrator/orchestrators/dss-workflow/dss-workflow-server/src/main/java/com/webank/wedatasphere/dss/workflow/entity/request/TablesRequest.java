package com.webank.wedatasphere.dss.workflow.entity.request;

import com.webank.wedatasphere.dss.common.label.LabelRouteVO;

public class TablesRequest {

    private String contextID;
    private String nodeName;
    private LabelRouteVO labels;

    public String getContextID() {
        return contextID;
    }

    public void setContextID(String contextID) {
        this.contextID = contextID;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public LabelRouteVO getLabels() {
        return labels;
    }

    public void setLabels(LabelRouteVO labels) {
        this.labels = labels;
    }
}
