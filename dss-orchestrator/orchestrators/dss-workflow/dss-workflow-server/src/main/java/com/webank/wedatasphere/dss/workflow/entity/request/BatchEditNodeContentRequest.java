package com.webank.wedatasphere.dss.workflow.entity.request;

import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.Map;

public class BatchEditNodeContentRequest {

    public static class NodeContent{

        private String nodeName;

        private String nodeContent;

        private Map<String,Object> nodeMetadata;


        public String getNodeName() {
            return nodeName;
        }

        public void setNodeName(String nodeName) {
            this.nodeName = nodeName;
        }

        public String getNodeContent() {
            return nodeContent;
        }

        public void setNodeContent(String nodeContent) {
            this.nodeContent = nodeContent;
        }

        public Map<String, Object> getNodeMetadata() {
            return nodeMetadata;
        }

        public void setNodeMetadata(Map<String, Object> nodeMetadata) {
            this.nodeMetadata = nodeMetadata;
        }
    }


    private Long workspaceId;

    private Long projectId;

    private Long orchestratorId;

    private String username;

    private List<NodeContent> nodeContentList;

    public Long getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(Long workspaceId) {
        this.workspaceId = workspaceId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getOrchestratorId() {
        return orchestratorId;
    }

    public void setOrchestratorId(Long orchestratorId) {
        this.orchestratorId = orchestratorId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<NodeContent> getNodeContentList() {
        return nodeContentList;
    }

    public void setNodeContentList(List<NodeContent> nodeContentList) {
        this.nodeContentList = nodeContentList;
    }


    @Override
    public String toString() {
        return "BatchEditNodeContentRequest{" +
                "workspaceId=" + workspaceId +
                ", projectId=" + projectId +
                ", orchestratorId=" + orchestratorId +
                ", username='" + username + '\'' +
                '}';
    }
}
