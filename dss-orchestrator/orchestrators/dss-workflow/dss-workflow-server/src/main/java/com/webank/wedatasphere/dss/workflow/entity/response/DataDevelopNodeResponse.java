package com.webank.wedatasphere.dss.workflow.entity.response;

import com.webank.wedatasphere.dss.workflow.entity.DataDevelopNodeInfo;

import java.util.ArrayList;
import java.util.List;

public class DataDevelopNodeResponse {

    private Long total = 0L;

    private List<DataDevelopNodeInfo> dataDevelopNodeInfoList = new ArrayList<>();


    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public List<DataDevelopNodeInfo> getDataDevelopNodeInfoList() {
        return dataDevelopNodeInfoList;
    }

    public void setDataDevelopNodeInfoList(List<DataDevelopNodeInfo> dataDevelopNodeInfoList) {
        this.dataDevelopNodeInfoList = dataDevelopNodeInfoList;
    }
}
