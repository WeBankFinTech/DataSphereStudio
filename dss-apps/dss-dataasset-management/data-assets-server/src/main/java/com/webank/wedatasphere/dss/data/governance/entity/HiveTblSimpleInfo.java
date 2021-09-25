package com.webank.wedatasphere.dss.data.governance.entity;

import lombok.Data;


@Data
public class HiveTblSimpleInfo {
    private String guid;
    private String name;
    private String qualifiedName;
    private String createTime;
    private String owner;
}
