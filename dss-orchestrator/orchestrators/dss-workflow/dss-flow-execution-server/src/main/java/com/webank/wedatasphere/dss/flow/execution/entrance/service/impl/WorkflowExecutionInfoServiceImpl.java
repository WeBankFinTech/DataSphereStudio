package com.webank.wedatasphere.dss.flow.execution.entrance.service.impl;

import com.webank.wedatasphere.dss.flow.execution.entrance.dao.WorkflowExecuteInfoMapper;
import com.webank.wedatasphere.dss.flow.execution.entrance.entity.WorkflowExecuteInfo;
import com.webank.wedatasphere.dss.flow.execution.entrance.entity.WorkflowExecuteInfoVo;
import com.webank.wedatasphere.dss.flow.execution.entrance.service.WorkflowExecutionInfoService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class WorkflowExecutionInfoServiceImpl implements WorkflowExecutionInfoService {

    @Autowired
    private WorkflowExecuteInfoMapper workflowExecuteInfoMapper;

    @Override
    public WorkflowExecuteInfo getExecuteInfoByFlowId(Long flowId) {
        return workflowExecuteInfoMapper.getExecuteInfoByFlowId(flowId);
    }

    @Override
    public void saveExecuteInfo(WorkflowExecuteInfoVo workflowExecuteInfoVo) {
        if (workflowExecuteInfoVo == null) {
            return;
        }
        WorkflowExecuteInfo executeInfo = new WorkflowExecuteInfo();
        executeInfo.setTaskId(workflowExecuteInfoVo.getTaskId());
        executeInfo.setFlowId(workflowExecuteInfoVo.getFlowId());
        executeInfo.setVersion(workflowExecuteInfoVo.getVersion());
        executeInfo.setStatus(workflowExecuteInfoVo.getStatus());
        executeInfo.setCreatetime(workflowExecuteInfoVo.getCreatetime());
        executeInfo.setFailedJobs(workflowExecuteInfoVo.getFailedJobsList().stream().map(map -> map.get("nodeID").toString()).collect(Collectors.joining(",")));
        executeInfo.setPendingJobs(workflowExecuteInfoVo.getPendingJobsList().stream().map(map -> map.get("nodeID").toString()).collect(Collectors.joining(",")));
        executeInfo.setSkippedJobs(workflowExecuteInfoVo.getSkippedJobsList().stream().map(map -> map.get("nodeID").toString()).collect(Collectors.joining(",")));
        executeInfo.setSucceedJobs(workflowExecuteInfoVo.getSucceedJobsList().stream().map(map -> map.get("nodeID").toString()).collect(Collectors.joining(",")));
        executeInfo.setRunningJobs(workflowExecuteInfoVo.getRunningJobsList().stream().map(map -> map.get("nodeID").toString()).collect(Collectors.joining(",")));

        WorkflowExecuteInfo executeInfoDB = workflowExecuteInfoMapper.getExecuteInfoByFlowIdAndVersion(workflowExecuteInfoVo.getFlowId(), workflowExecuteInfoVo.getVersion());
        if (executeInfoDB != null) {
            executeInfo.setId(executeInfoDB.getId());
            //成功的节点进行拼接
            executeInfo.setSucceedJobs(getSucceedJobsStr(executeInfo.getSucceedJobs(),executeInfoDB.getSucceedJobs()));
            workflowExecuteInfoMapper.updateWorkflowExecuteInfo(executeInfo);
        } else {
            workflowExecuteInfoMapper.insert(executeInfo);
        }
    }

    public String getSucceedJobsStr(String sucStr,String sucStrDB) {
        if(StringUtils.isBlank(sucStr) || StringUtils.isBlank(sucStrDB)){
            return StringUtils.isNotBlank(sucStr) ? sucStr : sucStrDB;
        }
        String[] nodeListArr = sucStrDB.split(",");
        for (int i = 0; i < nodeListArr.length; i++) {
            String nodeId = nodeListArr[i];
            if (StringUtils.isBlank(nodeId) || sucStr.contains(nodeId)) {
                continue;
            }
            if(!sucStr.endsWith(",")){
                sucStr = sucStr.concat(",");
            }
            sucStr = sucStr.concat(nodeId);
        }
        if(sucStr.endsWith(",")){
            sucStr = sucStr.substring(0,sucStr.length()-1);
        }
        return sucStr;
    }

    @Override
    public String getSucceedJobsByFlowId(Long flowId) {
        WorkflowExecuteInfo executeInfo = getExecuteInfoByFlowId(flowId);
        if (executeInfo != null) {
            String skippedJobs = executeInfo.getSkippedJobs();
            String succeedJobs = executeInfo.getSucceedJobs();
            //如果skippedjobs有内容，也需要添加上
            if (StringUtils.isNotEmpty(skippedJobs)) {
                if (StringUtils.isNotEmpty(succeedJobs)) {
                    return succeedJobs + "," + skippedJobs;
                }
                return skippedJobs;
            }
            return succeedJobs;
        }
        return null;
    }
}

