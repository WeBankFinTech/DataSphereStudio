package com.webank.wedatasphere.dss.git.common.protocol.response;

import com.webank.wedatasphere.dss.git.common.protocol.GitUserEntity;

import java.util.Map;

public class GitUserByWorkspaceResponse {
    private Map<String, GitUserEntity> map;

    public GitUserByWorkspaceResponse(Map<String, GitUserEntity> map) {
        this.map = map;
    }

    public GitUserByWorkspaceResponse() {
    }

    public Map<String, GitUserEntity> getMap() {
        return map;
    }

    public void setMap(Map<String, GitUserEntity> map) {
        this.map = map;
    }
}
