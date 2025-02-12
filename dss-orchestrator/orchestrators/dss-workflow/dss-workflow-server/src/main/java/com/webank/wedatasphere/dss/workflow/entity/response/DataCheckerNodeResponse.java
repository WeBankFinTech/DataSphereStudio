package com.webank.wedatasphere.dss.workflow.entity.response;


import com.webank.wedatasphere.dss.workflow.entity.DataCheckerNodeInfo;

import java.util.ArrayList;
import java.util.List;

public class DataCheckerNodeResponse {

    private Long total = 0L;

    private List<DataCheckerNodeInfo> dataCheckerNodeInfoList = new ArrayList<>();


    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }


    public List<DataCheckerNodeInfo> getDataCheckerNodeInfoList() {
        return dataCheckerNodeInfoList;
    }

    public void setDataCheckerNodeInfoList(List<DataCheckerNodeInfo> dataCheckerNodeInfoList) {
        this.dataCheckerNodeInfoList = dataCheckerNodeInfoList;
    }
}
