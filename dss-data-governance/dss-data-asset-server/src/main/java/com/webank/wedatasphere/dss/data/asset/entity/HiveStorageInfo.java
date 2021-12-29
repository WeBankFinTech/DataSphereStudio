package com.webank.wedatasphere.dss.data.asset.entity;

import lombok.Data;

/**
 * @Author:李嘉玮
 */
@Data
public class HiveStorageInfo {
    private  String tableName;
    private  Long storage;
    private  String guid;
}