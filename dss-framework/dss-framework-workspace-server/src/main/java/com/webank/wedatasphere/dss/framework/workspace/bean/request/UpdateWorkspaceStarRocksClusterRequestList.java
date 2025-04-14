package com.webank.wedatasphere.dss.framework.workspace.bean.request;

import java.util.List;

public class UpdateWorkspaceStarRocksClusterRequestList {

    private List<UpdateWorkspaceStarRocksClusterRequest> starRocksUpdateRequest;

    public List<UpdateWorkspaceStarRocksClusterRequest> getStarRocksUpdateRequest() {
        return starRocksUpdateRequest;
    }

    public void setStarRocksUpdateRequest(List<UpdateWorkspaceStarRocksClusterRequest> starRocksUpdateRequest) {
        this.starRocksUpdateRequest = starRocksUpdateRequest;
    }
}
