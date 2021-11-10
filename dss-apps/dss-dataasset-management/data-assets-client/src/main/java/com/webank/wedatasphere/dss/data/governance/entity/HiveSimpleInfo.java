package com.webank.wedatasphere.dss.data.governance.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;


@Data
public class HiveSimpleInfo implements Serializable {
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
