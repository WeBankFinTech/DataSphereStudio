package com.webank.wedatasphere.dss.data.asset.entity;

import lombok.Data;

import java.util.List;
import java.util.Set;

/**
 * @author suyc
 * @Classname HiveTblSimpleInfo
 * @Description TODO
 * @Date 2021/8/24 10:17
 * @Created by suyc
 */
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
