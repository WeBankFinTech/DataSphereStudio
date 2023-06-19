package com.webank.wedatasphere.dss.workflow.entity.vo;

import com.webank.wedatasphere.dss.workflow.common.entity.DSSFlow;
import com.webank.wedatasphere.dss.workflow.common.entity.DSSFlowRelation;

import java.util.List;

public class FlowInfoVo {

    List<DSSFlowRelation> subFlowList;

    Long parentFlowId;

    public List<DSSFlowRelation> getSubFlowList() {
        return subFlowList;
    }

    public void setSubFlowList(List<DSSFlowRelation> subFlowList) {
        this.subFlowList = subFlowList;
    }

    public FlowInfoVo() {
    }
    
    public Long getParentFlowId() {
        return parentFlowId;
    }

    public void setParentFlowId(Long parentFlowId) {
        this.parentFlowId = parentFlowId;
    }
}
