package com.webank.wedatasphere.dss.framework.workspace.bean.request;

import java.io.Serializable;

public class UpdateWorkspaceStarRocksClusterRequest implements Serializable {

    private  Long workspaceId;

    private String workspaceName;

    private String username;

    private String clusterName;

    private String clusterIp;

    private String httpPort;

    private String tcpPort;

    private Boolean isDefaultCluster;

    public UpdateWorkspaceStarRocksClusterRequest() {
    }

    public UpdateWorkspaceStarRocksClusterRequest(Long workspaceId, String workspaceName, String username, String clusterName, String clusterIp, String httpPort, String tcpPort, Boolean isDefaultCluster) {
        this.workspaceId = workspaceId;
        this.workspaceName = workspaceName;
        this.username = username;
        this.clusterName = clusterName;
        this.clusterIp = clusterIp;
        this.httpPort = httpPort;
        this.tcpPort = tcpPort;
        this.isDefaultCluster = isDefaultCluster;
    }

    public Long getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(Long workspaceId) {
        this.workspaceId = workspaceId;
    }

    public String getWorkspaceName() {
        return workspaceName;
    }

    public void setWorkspaceName(String workspaceName) {
        this.workspaceName = workspaceName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getClusterIp() {
        return clusterIp;
    }

    public void setClusterIp(String clusterIp) {
        this.clusterIp = clusterIp;
    }

    public String getHttpPort() {
        return httpPort;
    }

    public void setHttpPort(String httpPort) {
        this.httpPort = httpPort;
    }

    public String getTcpPort() {
        return tcpPort;
    }

    public void setTcpPort(String tcpPort) {
        this.tcpPort = tcpPort;
    }

    public Boolean getDefaultCluster() {
        return isDefaultCluster;
    }

    public void setDefaultCluster(Boolean defaultCluster) {
        isDefaultCluster = defaultCluster;
    }

    @Override
    public String toString() {
        return "UpdateWorkspaceStarRocksClusterRequest{" +
                "workspaceId=" + workspaceId +
                ", workspaceName='" + workspaceName + '\'' +
                ", username='" + username + '\'' +
                ", clusterName='" + clusterName + '\'' +
                ", clusterIp='" + clusterIp + '\'' +
                ", httpPort=" + httpPort +
                ", tcpPort=" + tcpPort +
                ", isDefaultCluster=" + isDefaultCluster +
                '}';
    }
}