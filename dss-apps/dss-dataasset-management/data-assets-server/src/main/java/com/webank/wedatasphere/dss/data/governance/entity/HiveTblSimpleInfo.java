package com.webank.wedatasphere.dss.data.governance.entity;

import lombok.Data;

import java.util.List;


@Data
public class HiveTblSimpleInfo {
    private String guid;
    private String name;
    private String qualifiedName;
    private String createTime;
    private String owner;
    private String aliases;
    private String lastAccessTime;
    private String comment;
    private List<String> classifications;
    private String totalSize;
}
