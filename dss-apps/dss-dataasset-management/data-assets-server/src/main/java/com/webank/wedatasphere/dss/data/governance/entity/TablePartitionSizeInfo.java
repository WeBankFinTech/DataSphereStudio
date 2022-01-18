package com.webank.wedatasphere.dss.data.governance.entity;

import lombok.Data;

@Data
public class TablePartitionSizeInfo {
    private Long id;
    private Long parId;
    private Long lastAccessTime;
    private Long sdId;
    private String dbName;
    private String tblName;
    private String paramKey;
    private String paramValue;
}
