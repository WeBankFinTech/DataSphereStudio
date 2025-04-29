package com.webank.wedatasphere.dss.framework.workspace.bean;

import java.util.Date;

public class DSSWorkspaceStarRocksCluster {

    private Integer id;

    private  Long workspaceId;

    private String workspaceName;

    private String clusterName;

    private String clusterIp;

    private String httpPort;

    private String tcpPort;

    private Boolean isDefaultCluster;

    private String createUser;

    private String updateUser;

    private Date createTime;

    private Date updateTime;

    public DSSWorkspaceStarRocksCluster(Long workspaceId, String workspaceName, String clusterName, String clusterIp, String httpPort, String tcpPort, Boolean isDefaultCluster, String createUser, String updateUser, Date createTime, Date updateTime) {
        this.workspaceId = workspaceId;
        this.workspaceName = workspaceName;
        this.clusterName = clusterName;
        this.clusterIp = clusterIp;
        this.httpPort = httpPort;
        this.tcpPort = tcpPort;
        this.isDefaultCluster = isDefaultCluster;
        this.createUser = createUser;
        this.updateUser = updateUser;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    public DSSWorkspaceStarRocksCluster() {
    }

    public DSSWorkspaceStarRocksCluster(Integer id, Long workspaceId, String workspaceName, String clusterName, String clusterIp, String httpPort, String tcpPort, Boolean isDefaultCluster, String createUser, String updateUser, Date createTime, Date updateTime) {
        this.id = id;
        this.workspaceId = workspaceId;
        this.workspaceName = workspaceName;
        this.clusterName = clusterName;
        this.clusterIp = clusterIp;
        this.httpPort = httpPort;
        this.tcpPort = tcpPort;
        this.isDefaultCluster = isDefaultCluster;
        this.createUser = createUser;
        this.updateUser = updateUser;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "DSSWorkspaceStarRocksCluster{" +
                "id=" + id +
                ", workspaceId=" + workspaceId +
                ", workspaceName='" + workspaceName + '\'' +
                ", clusterName='" + clusterName + '\'' +
                ", clusterIp='" + clusterIp + '\'' +
                ", httpPort=" + httpPort +
                ", tcpPort=" + tcpPort +
                ", isDefaultCluster=" + isDefaultCluster +
                ", createUser='" + createUser + '\'' +
                ", updateUser='" + updateUser + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}