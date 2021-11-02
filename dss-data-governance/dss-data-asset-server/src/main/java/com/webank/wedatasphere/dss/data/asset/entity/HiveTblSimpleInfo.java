package com.webank.wedatasphere.dss.data.asset.entity;

import lombok.Data;

import java.util.List;

/**
 * @Classname HiveTblSimpleInfo
 * @Description TODO
 * @Date 2021/8/24 10:17
 * @Created by suyc
 */
@Data
public class HiveTblSimpleInfo {
    private String guid;
    private String name;
    private String qualifiedName;
    private String createTime;
    private String owner;
    private  String dbName;
    private List<HiveTblDetailInfo.HiveClassificationInfo> classifications;

}
