package com.webank.wedatasphere.dss.git.common.protocol.response;


import com.webank.wedatasphere.dss.git.common.protocol.GitSearchResult;

import java.util.List;
import java.util.Map;

public class GitSearchResponse{
    private List<GitSearchResult> result;
    private Integer total;

    public GitSearchResponse(List<GitSearchResult> result, Integer total) {
        this.result = result;
        this.total = total;
    }

    public GitSearchResponse() {
    }

    public List<GitSearchResult> getResult() {
        return result;
    }

    public void setResult(List<GitSearchResult> result) {
        this.result = result;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }
}
