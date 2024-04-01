package com.webank.wedatasphere.dss.git.common.protocol.response;

import java.util.List;

/**
 * @author zhaobincai
 * @date 2024/3/28 15:06
 */
public class GitDeleteResponse {
    private String projectName;

    private List<String> path;

    public GitDeleteResponse(String projectName, List<String> path) {
        this.projectName = projectName;
        this.path = path;
    }

    public GitDeleteResponse() {
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public List<String> getPath() {
        return path;
    }

    public void setPath(List<String> path) {
        this.path = path;
    }
}
