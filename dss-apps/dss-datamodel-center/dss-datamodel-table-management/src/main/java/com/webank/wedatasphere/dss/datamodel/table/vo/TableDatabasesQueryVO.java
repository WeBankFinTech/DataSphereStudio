package com.webank.wedatasphere.dss.datamodel.table.vo;


import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class TableDatabasesQueryVO {
    private String name;

    private Integer pageSize = 20;

    private Integer pageNum = 1;

    private String user;
}
