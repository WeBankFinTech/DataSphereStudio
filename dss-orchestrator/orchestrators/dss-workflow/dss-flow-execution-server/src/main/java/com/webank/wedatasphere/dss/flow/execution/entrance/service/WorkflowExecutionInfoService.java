package com.webank.wedatasphere.dss.flow.execution.entrance.service;

import com.webank.wedatasphere.dss.flow.execution.entrance.entity.WorkflowExecuteInfo;
import com.webank.wedatasphere.dss.flow.execution.entrance.entity.WorkflowExecuteInfoVo;

public interface WorkflowExecutionInfoService {

    WorkflowExecuteInfo getExecuteInfoByFlowId(Long flowId);

    void saveExecuteInfo(WorkflowExecuteInfoVo workflowExecuteInfoVo);

    String getSucceedJobsByFlowId(Long flowId);

}


