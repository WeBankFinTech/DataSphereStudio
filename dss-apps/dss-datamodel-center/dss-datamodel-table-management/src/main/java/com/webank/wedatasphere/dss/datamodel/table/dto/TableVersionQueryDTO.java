package com.webank.wedatasphere.dss.datamodel.table.dto;


import lombok.Data;

import java.util.Date;

@Data
public class TableVersionQueryDTO {

    private Long id;

    private Long tblId;

    private String name;

    /**
     * 是否物化
     */
    private Integer isMaterialized;

    /**
     * 创建table的sql
     */
    private String tableCode;

    /**
     * 版本注释
     */
    private String comment;

    /**
     * 版本信息：默认 1
     */
    private String version;

    private String tableParams;

    private String columns;

    /**
     * rollback,update,add
     */
    private String sourceType;

    private Date createTime;

    private Date updateTime;

    private String creator;
}
