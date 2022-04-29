package com.webank.wedatasphere.dss.data.asset.entity;

import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class HiveTblSimpleInfo {
    private String guid;
    private String name;
    private String dbName;
    private String qualifiedName;
    private List<String> columns;
    private String createTime;
    private String owner;
    private String comment;
    private Set<String> labels;
    private List<HiveTblDetailInfo.HiveClassificationInfo> classifications;
}
