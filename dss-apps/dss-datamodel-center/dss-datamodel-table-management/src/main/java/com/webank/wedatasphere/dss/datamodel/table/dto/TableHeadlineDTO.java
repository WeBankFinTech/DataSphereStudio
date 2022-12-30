package com.webank.wedatasphere.dss.datamodel.table.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class TableHeadlineDTO {

    /**
     * 0 表示 Hive
     */
    private Integer storageType;

    /**
     * 0 表示离线表
     */
    private Integer tableType;


    /**
     * 0 逻辑表 1 物理表
     */
    private Integer entityType;
}
