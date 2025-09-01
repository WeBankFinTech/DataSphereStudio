package com.webank.wedatasphere.dss.common.protocol.workspace;

import com.webank.wedatasphere.dss.common.entity.workspace.DSSStarRocksCluster;

import java.util.List;

public class StarRocksClusterListResponse {

    private List<DSSStarRocksCluster> list;

    public StarRocksClusterListResponse(List<DSSStarRocksCluster> list) {
        this.list = list;
    }

    public StarRocksClusterListResponse() {
    }

    public List<DSSStarRocksCluster> getList() {
        return list;
    }

    public void setList(List<DSSStarRocksCluster> list) {
        this.list = list;
    }
}
