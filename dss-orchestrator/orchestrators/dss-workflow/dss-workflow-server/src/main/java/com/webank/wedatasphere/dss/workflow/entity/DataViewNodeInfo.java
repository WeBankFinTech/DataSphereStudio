package com.webank.wedatasphere.dss.workflow.entity;

import java.util.Date;
import java.util.Map;

public class DataViewNodeInfo extends  NodeBaseInfo {


    // 视图ID
    private String viewId;

    // 节点描述
    private String nodeDesc;

    public String getViewId() {
        return viewId;
    }

    public void setViewId(String viewId) {
        this.viewId = viewId;
    }

    public String getNodeDesc() {
        return nodeDesc;
    }

    public void setNodeDesc(String nodeDesc) {
        this.nodeDesc = nodeDesc;
    }
}
