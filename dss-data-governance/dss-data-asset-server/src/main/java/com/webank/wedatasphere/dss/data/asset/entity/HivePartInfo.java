package com.webank.wedatasphere.dss.data.asset.entity;

import lombok.Data;

/**
 * @Author:李嘉玮
 */
@Data
public class HivePartInfo {
    private  String partName;
    private  int reordCnt;
    private  int store;
    private  String createTime;
    private  String lastAccessTime;

}