package com.webank.wedatasphere.dss.git.common.protocol.response;

import java.util.List;

public class GitHistoryResponse {
    private List<GitCommitResponse> responses;

    public GitHistoryResponse(List<GitCommitResponse> responses) {
        this.responses = responses;
    }

    public GitHistoryResponse() {
    }

    public List<GitCommitResponse> getResponses() {
        return responses;
    }

    public void setResponses(List<GitCommitResponse> responses) {
        this.responses = responses;
    }
}
