package com.webank.wedatasphere.dss.data.governance.entity;

import lombok.Data;


@Data
public class PartInfo {
    private  String partName;
    private  int reordCnt;
    private  int store;
    private  String createTime;
    private  String lastAccessTime;
    private int fileCount;
}