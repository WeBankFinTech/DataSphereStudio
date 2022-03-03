package com.webank.wedatasphere.dss.data.governance.entity;


import lombok.Data;

@Data
public class TableColumnCount {
    private String dbName;
    private String tblName;
    private Long tblId;
    private Integer columnCount;
}
