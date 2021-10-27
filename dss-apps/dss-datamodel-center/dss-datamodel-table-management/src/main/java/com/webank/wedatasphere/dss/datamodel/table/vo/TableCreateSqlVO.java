package com.webank.wedatasphere.dss.datamodel.table.vo;


import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class TableCreateSqlVO {
    private Long tableId;

    private String guid;

    private String user;
}
