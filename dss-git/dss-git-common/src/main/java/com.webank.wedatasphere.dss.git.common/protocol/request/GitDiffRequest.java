package com.webank.wedatasphere.dss.git.common.protocol.request;

import com.webank.wedatasphere.dss.common.entity.BmlResource;

import java.util.Map;


public class GitDiffRequest extends GitBaseRequest{
    /**
     * 待修改的文件（key：文件相对路径，value：文件对应BML）
     */
    private Map<String, BmlResource> bmlResourceMap;
    /**
     * 下载BMLReource使用的用户名
     */
    private String username;

    public GitDiffRequest() {
    }

    public GitDiffRequest(Map<String, BmlResource> bmlResourceMap, String username) {
        this.bmlResourceMap = bmlResourceMap;
        this.username = username;
    }

    public GitDiffRequest(Long workspaceId, String projectName, Map<String, BmlResource> bmlResourceMap, String username) {
        super(workspaceId, projectName);
        this.bmlResourceMap = bmlResourceMap;
        this.username = username;
    }


    public Map<String, BmlResource> getBmlResourceMap() {
        return bmlResourceMap;
    }

    public void setBmlResourceMap(Map<String, BmlResource> bmlResourceMap) {
        this.bmlResourceMap = bmlResourceMap;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
