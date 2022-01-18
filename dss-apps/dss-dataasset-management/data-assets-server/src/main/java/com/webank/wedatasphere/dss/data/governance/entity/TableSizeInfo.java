package com.webank.wedatasphere.dss.data.governance.entity;


import lombok.Data;

@Data
public class TableSizeInfo {
    private Long id;
    private String dbName;
    private String tblName;
    private String paramKey;
    private String paramValue;
}
