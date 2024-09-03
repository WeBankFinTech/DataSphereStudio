package com.webank.wedatasphere.dss.workflow.entity.response;

import com.webank.wedatasphere.dss.workflow.entity.DataDevelopNodeInfo;
import com.webank.wedatasphere.dss.workflow.entity.DataViewNodeInfo;

import java.util.ArrayList;
import java.util.List;

public class DataViewNodeResponse {


    private Long total = 0L;

    private List<DataViewNodeInfo> dataDevelopNodeInfoList = new ArrayList<>();


    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public List<DataViewNodeInfo> getDataDevelopNodeInfoList() {
        return dataDevelopNodeInfoList;
    }

    public void setDataDevelopNodeInfoList(List<DataViewNodeInfo> dataDevelopNodeInfoList) {
        this.dataDevelopNodeInfoList = dataDevelopNodeInfoList;
    }
}
