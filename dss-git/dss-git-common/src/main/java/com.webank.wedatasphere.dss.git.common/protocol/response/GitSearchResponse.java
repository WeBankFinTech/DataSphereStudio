package com.webank.wedatasphere.dss.git.common.protocol.response;


import java.util.List;
import java.util.Map;

public class GitSearchResponse{
    private Map<String, List<String>> result;
    private Integer total;

    public GitSearchResponse(Map<String, List<String>> result, Integer total) {
        this.result = result;
        this.total = total;
    }

    public GitSearchResponse() {
    }

    public Map<String, List<String>> getResult() {
        return result;
    }

    public void setResult(Map<String, List<String>> result) {
        this.result = result;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }
}
