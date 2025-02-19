package com.webank.wedatasphere.dss.git.common.protocol.request;

import com.webank.wedatasphere.dss.git.common.protocol.response.GitFileContentResponse;

import java.util.List;

public class GitDiffFileContentResponse {


    private List<GitFileContentResponse> gitFileContentResponseList;


    public GitDiffFileContentResponse(List<GitFileContentResponse> gitFileContentResponseList) {
        this.gitFileContentResponseList = gitFileContentResponseList;
    }

    public List<GitFileContentResponse> getGitFileContentResponseList() {
        return gitFileContentResponseList;
    }

    public void setGitFileContentResponseList(List<GitFileContentResponse> gitFileContentResponseList) {
        this.gitFileContentResponseList = gitFileContentResponseList;
    }
}
