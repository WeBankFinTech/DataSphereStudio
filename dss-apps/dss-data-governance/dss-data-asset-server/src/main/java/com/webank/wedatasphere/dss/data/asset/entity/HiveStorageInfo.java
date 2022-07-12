package com.webank.wedatasphere.dss.data.asset.entity;

import lombok.Data;

@Data
public class HiveStorageInfo {
    private  String tableName;
    private  Long storage;
    private  String guid;
}