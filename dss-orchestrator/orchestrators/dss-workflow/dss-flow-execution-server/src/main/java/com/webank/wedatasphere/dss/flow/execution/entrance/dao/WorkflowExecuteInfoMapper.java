package com.webank.wedatasphere.dss.flow.execution.entrance.dao;

import com.webank.wedatasphere.dss.flow.execution.entrance.entity.WorkflowExecuteInfo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface WorkflowExecuteInfoMapper {

    int insert(WorkflowExecuteInfo workflowExecuteInfo);

    int updateWorkflowExecuteInfo(WorkflowExecuteInfo workflowExecuteInfo);

    WorkflowExecuteInfo getExecuteInfoByFlowId(@Param("flowId") Long flowId);

    WorkflowExecuteInfo getExecuteInfoByFlowIdAndVersion(@Param("flowId") Long flowId,@Param("version") String version);

}
