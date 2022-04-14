package com.webank.wedatasphere.dss.workflow.io.scheduler;

import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.common.label.DSSLabel;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.workflow.common.entity.DSSFlow;
import com.webank.wedatasphere.dss.workflow.io.input.NodeInputService;
import com.webank.wedatasphere.dss.workflow.scheduler.DssJob;
import org.apache.commons.collections.CollectionUtils;
import org.apache.linkis.server.BDPJettyServerHelper;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class NodeImportJob extends DssJob {

    private NodeInputService nodeInputService;

    private ImportJobEntity jobEntity;

    List<Map<String, Object>> nodeJsonListRes;

    public NodeInputService getNodeInputService() {
        return nodeInputService;
    }

    public void setNodeInputService(NodeInputService nodeInputService) {
        this.nodeInputService = nodeInputService;
    }

    public ImportJobEntity getJobEntity() {
        return jobEntity;
    }

    public void setJobEntity(ImportJobEntity jobEntity) {
        this.jobEntity = jobEntity;
    }

    public List<Map<String, Object>> getNodeJsonListRes() {
        return nodeJsonListRes;
    }

    public void setNodeJsonListRes(List<Map<String, Object>> nodeJsonListRes) {
        this.nodeJsonListRes = nodeJsonListRes;
    }


    @Override
    public void run() {
        try {
            // TODO: 2020/3/20 暂时注视掉appconn相关
            String updateNodeJson = nodeInputService.uploadResourceToBml(jobEntity.getUserName(), jobEntity.getNodeJson(),
                    jobEntity.getWorkFlowResourceSavePath(), jobEntity.getProjectName());
            updateNodeJson = nodeInputService.uploadAppConnResource(jobEntity.getUserName(), jobEntity.getProjectName(),
                    jobEntity.getDssFlow(), updateNodeJson, jobEntity.getUpdateContextId(), jobEntity.getAppConnResourceSavePath(),
                    jobEntity.getWorkspace(), jobEntity.getOrcVersion(), jobEntity.getDssLabels());
            //兼容0.x的key修改
            if (updateNodeJson.contains("wds.linkis.yarnqueue")) {
                updateNodeJson = updateNodeJson.replace("wds.linkis.yarnqueue", "wds.linkis.rm.yarnqueue");
            }
            Map<String, Object> nodeJsonMap = BDPJettyServerHelper.jacksonJson().readValue(updateNodeJson, Map.class);
            //更新subflowID
            String nodeType = nodeJsonMap.get("jobType").toString();
            if (nodeType.contains("appjoint")) {
                nodeJsonMap.replace("jobType", nodeType.replace("appjoint", "appconn"));
            }
            if ("workflow.subflow".equals(nodeType) && CollectionUtils.isNotEmpty(jobEntity.getSubflows())) {
                String subFlowName = nodeJsonMap.get("title").toString();
                logger.info("subflows:{}", jobEntity.getSubflows());
                List<DSSFlow> dssFlowList = jobEntity.getSubflows().stream().filter(subflow -> subflow.getName().equals(subFlowName)).collect(Collectors.toList());
                if (dssFlowList.size() == 1) {
                    updateNodeJson = nodeInputService.updateNodeSubflowID(updateNodeJson, dssFlowList.get(0).getId());
                    nodeJsonMap = BDPJettyServerHelper.jacksonJson().readValue(updateNodeJson, Map.class);
                    nodeJsonListRes.add(nodeJsonMap);
                } else if (dssFlowList.size() > 1) {
                    logger.error("工程内存在重复的子工作流节点名称，导入失败" + subFlowName);
                    throw new DSSErrorException(90077, "工程内存在重复的子工作流节点名称，导入失败" + subFlowName);
                } else {
                    logger.error("工程内未能找到子工作流节点，导入失败" + subFlowName);
                    throw new DSSErrorException(90078, "工程内未能找到子工作流节点，导入失败" + subFlowName);
                }
            } else {
                nodeJsonListRes.add(nodeJsonMap);
            }
        } catch (Exception e) {
            //todo 失败重试
            logger.error("failed to import node:", e);
        }

    }


    public static class ImportJobEntity {
        private String userName;
        private String projectName;
        private DSSFlow dssFlow;
        private List<DSSFlow> subflows;
        private String workFlowResourceSavePath;
        private String appConnResourceSavePath;
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

        public String getWorkFlowResourceSavePath() {
            return workFlowResourceSavePath;
        }

        public void setWorkFlowResourceSavePath(String workFlowResourceSavePath) {
            this.workFlowResourceSavePath = workFlowResourceSavePath;
        }

        public String getAppConnResourceSavePath() {
            return appConnResourceSavePath;
        }

        public void setAppConnResourceSavePath(String appConnResourceSavePath) {
            this.appConnResourceSavePath = appConnResourceSavePath;
        }

        public List<DSSFlow> getSubflows() {
            return subflows;
        }

        public void setSubflows(List<DSSFlow> subflows) {
            this.subflows = subflows;
        }
    }
}
