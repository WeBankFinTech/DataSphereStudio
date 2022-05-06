package com.webank.wedatasphere.dss.workflow.io.scheduler;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.webank.wedatasphere.dss.common.entity.node.DSSNode;
import com.webank.wedatasphere.dss.common.entity.node.DSSNodeDefault;
import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.common.label.DSSLabel;
import com.webank.wedatasphere.dss.common.utils.IoUtils;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.workflow.common.entity.DSSFlow;
import com.webank.wedatasphere.dss.workflow.dao.NodeInfoMapper;
import com.webank.wedatasphere.dss.workflow.entity.CommonAppConnNode;
import com.webank.wedatasphere.dss.workflow.entity.NodeInfo;
import com.webank.wedatasphere.dss.workflow.io.export.NodeExportService;
import com.webank.wedatasphere.dss.workflow.io.input.NodeInputService;
import com.webank.wedatasphere.dss.workflow.scheduler.DssJob;
import com.webank.wedatasphere.dss.workflow.service.WorkflowNodeService;
import org.apache.linkis.server.BDPJettyServerHelper;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class NodeCopyJob extends DssJob {

    private NodeInputService nodeInputService;

    private NodeExportService nodeExportService;

    private NodeInfoMapper nodeInfoMapper;

    private WorkflowNodeService workflowNodeService;

    private JobEntity jobEntity;

    private List<Map<String, Object>> nodeJsonListRes;

    private AtomicInteger failedCount;

    private CountDownLatch countDownLatch;

    @Override
    public void run() {
        try {
            //重新上传一份jar文件到bml
            String updateNodeJson = inputNodeFiles(jobEntity.userName, jobEntity.projectName, jobEntity.nodeJson);

            Map<String, Object> nodeJsonMap = BDPJettyServerHelper.jacksonJson().readValue(updateNodeJson, Map.class);
            //更新subflowID
            String nodeType = nodeJsonMap.get("jobType").toString();
            NodeInfo nodeInfo = nodeInfoMapper.getWorkflowNodeByType(nodeType);
            if ("workflow.subflow".equals(nodeType)) {
                String subFlowName = nodeJsonMap.get("title").toString();
                List<DSSFlow> dssFlowList = jobEntity.getSubflows().stream().filter(subflow ->
                        subflow.getName().equals(subFlowName)
                ).collect(Collectors.toList());
                if (dssFlowList.size() == 1) {
                    updateNodeJson = nodeInputService.updateNodeSubflowID(updateNodeJson, dssFlowList.get(0).getId());
                    nodeJsonMap = BDPJettyServerHelper.jacksonJson().readValue(updateNodeJson, Map.class);
                    nodeJsonListRes.add(nodeJsonMap);
                } else if (dssFlowList.size() > 1) {
                    logger.error("工程内存在重复的子工作流节点名称，导入失败" + subFlowName);
                    throw new DSSErrorException(90077, "工程内存在重复的子工作流节点名称，导入失败" + subFlowName);
                } else {
                    logger.error("工程内存在重复的子工作流节点名称，导入失败" + subFlowName);
                    throw new DSSErrorException(90078, "工程内未能找到子工作流节点，导入失败" + subFlowName);
                }
//            } else if (nodeJsonMap.get("jobContent") != null && !((Map) nodeJsonMap.get("jobContent")).containsKey("script")) {
            } else if (Boolean.TRUE.equals(nodeInfo.getSupportJump()) && nodeInfo.getJumpType() == 1) {
                logger.info("nodeJsonMap.jobContent is:{}", nodeJsonMap.get("jobContent"));
                CommonAppConnNode newNode = new CommonAppConnNode();
                CommonAppConnNode oldNode = new CommonAppConnNode();
                oldNode.setJobContent((Map<String, Object>) nodeJsonMap.get("jobContent"));
                oldNode.setContextId(jobEntity.getUpdateContextId());
                oldNode.setNodeType(nodeType);
                oldNode.setName((String) nodeJsonMap.get("title"));
                oldNode.setFlowId(jobEntity.getDssFlow().getId());
                oldNode.setWorkspace(jobEntity.getWorkspace());
                oldNode.setDssLabels(jobEntity.getDssLabels());
                oldNode.setFlowName(jobEntity.getDssFlow().getName());
                oldNode.setProjectId(jobEntity.getDssFlow().getProjectID());
                newNode.setName(oldNode.getName());
                Map<String, Object> jobContent = workflowNodeService.copyNode(jobEntity.userName, newNode, oldNode, jobEntity.orcVersion);
                nodeJsonMap.put("jobContent", jobContent);
                nodeJsonListRes.add(nodeJsonMap);
            } else {
                nodeJsonListRes.add(nodeJsonMap);
            }
        } catch (Exception e) {
            failedCount.getAndAdd(1);
            logger.error("failed to copy node:", e);
        } finally {
            countDownLatch.countDown();
        }
    }

    //由于每一个节点可能含有jar文件，这个功能不能直接复制使用，因为删掉新版本节点会直接删掉旧版本的node中的jar文件
    //所以重新上传一份jar文件到bml
    private String inputNodeFiles(String userName, String projectName, String nodeJson) throws IOException {
        String flowPath = IoUtils.generateIOPath(userName, projectName, "");
        String workFlowResourceSavePath = flowPath + File.separator + "resource" + File.separator;
        Gson gson = new Gson();
        JsonParser parser = new JsonParser();
        JsonObject jsonObject = parser.parse(nodeJson).getAsJsonObject();
        DSSNode node = gson.fromJson(jsonObject, new TypeToken<DSSNodeDefault>() {
        }.getType());
        //先导出来
        nodeExportService.downloadNodeResourceToLocal(userName, node, workFlowResourceSavePath);
        //后导入到bml
        String updateNodeJson = nodeInputService.uploadResourceToBml(userName, nodeJson, workFlowResourceSavePath, projectName);
        return updateNodeJson;
    }

    public NodeInputService getNodeInputService() {
        return nodeInputService;
    }

    public void setNodeInputService(NodeInputService nodeInputService) {
        this.nodeInputService = nodeInputService;
    }

    public JobEntity getJobEntity() {
        return jobEntity;
    }

    public void setJobEntity(JobEntity jobEntity) {
        this.jobEntity = jobEntity;
    }

    public List<Map<String, Object>> getNodeJsonListRes() {
        return nodeJsonListRes;
    }

    public void setNodeJsonListRes(List<Map<String, Object>> nodeJsonListRes) {
        this.nodeJsonListRes = nodeJsonListRes;
    }

    public CountDownLatch getCountDownLatch() {
        return countDownLatch;
    }

    public void setCountDownLatch(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }

    public AtomicInteger getFailedCount() {
        return failedCount;
    }

    public void setFailedCount(AtomicInteger failedCount) {
        this.failedCount = failedCount;
    }

    public NodeExportService getNodeExportService() {
        return nodeExportService;
    }

    public void setNodeExportService(NodeExportService nodeExportService) {
        this.nodeExportService = nodeExportService;
    }

    public NodeInfoMapper getNodeInfoMapper() {
        return nodeInfoMapper;
    }

    public void setNodeInfoMapper(NodeInfoMapper nodeInfoMapper) {
        this.nodeInfoMapper = nodeInfoMapper;
    }

    public WorkflowNodeService getWorkflowNodeService() {
        return workflowNodeService;
    }

    public void setWorkflowNodeService(WorkflowNodeService workflowNodeService) {
        this.workflowNodeService = workflowNodeService;
    }


    public static class JobEntity {
        private String userName;
        private String projectName;
        private DSSFlow dssFlow;
        private List<DSSFlow> subflows;
        private Workspace workspace;
        private String orcVersion;
        private List<DSSLabel> dssLabels;
        private String nodeJson;
        private String updateContextId;

        public String getUpdateContextId() {
            return updateContextId;
        }

        public void setUpdateContextId(String updateContextId) {
            this.updateContextId = updateContextId;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getProjectName() {
            return projectName;
        }

        public void setProjectName(String projectName) {
            this.projectName = projectName;
        }

        public DSSFlow getDssFlow() {
            return dssFlow;
        }

        public void setDssFlow(DSSFlow dssFlow) {
            this.dssFlow = dssFlow;
        }

        public Workspace getWorkspace() {
            return workspace;
        }

        public void setWorkspace(Workspace workspace) {
            this.workspace = workspace;
        }

        public String getOrcVersion() {
            return orcVersion;
        }

        public void setOrcVersion(String orcVersion) {
            this.orcVersion = orcVersion;
        }

        public List<DSSLabel> getDssLabels() {
            return dssLabels;
        }

        public void setDssLabels(List<DSSLabel> dssLabels) {
            this.dssLabels = dssLabels;
        }

        public String getNodeJson() {
            return nodeJson;
        }

        public void setNodeJson(String nodeJson) {
            this.nodeJson = nodeJson;
        }

        public List<DSSFlow> getSubflows() {
            return subflows;
        }

        public void setSubflows(List<DSSFlow> subflows) {
            this.subflows = subflows;
        }
    }
}
