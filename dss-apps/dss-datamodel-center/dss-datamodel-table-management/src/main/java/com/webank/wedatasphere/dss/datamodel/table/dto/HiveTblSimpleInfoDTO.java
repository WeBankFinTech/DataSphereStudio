package com.webank.wedatasphere.dss.datamodel.table.dto;


import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class HiveTblSimpleInfoDTO {
    private String guid;
    private String name;
    private String qualifiedName;
    private Date createTime;
    private String owner;
    private Date lastAccessTime;
    private String aliases;
    private String comment;
    private List<String> classifications;
    private String totalSize;
}
