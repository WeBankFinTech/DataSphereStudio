package com.webank.wedatasphere.dss.datamodel.table.dto;

import lombok.Data;

import java.util.Date;


@Data
public class PartInfoDTO {
    private String partName;
    private int reordCnt;
    private int store;
    private int fileCount;
    private Date createTime;
    private Date lastAccessTime;
}