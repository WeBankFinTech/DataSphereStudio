package com.webank.wedatasphere.dss.data.asset.entity;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

@Data
public class HiveTblDetailInfo implements Serializable {
    private HiveTblBasicInfo basic;
    private List<HiveColumnInfo> columns;
    private List<HiveColumnInfo> partitionKeys;
    private List<HiveClassificationInfo> classifications;

    @Data
    public static class HiveTblBasicInfo extends HiveTblSimpleInfo {
        private String store;     //存储量
        private Boolean isParTbl;     //是否分区表
        private String tableType;    //Hive表类型 tableType: EXTERNAL_TABLE, MANAGED_TABLE
        private String location;     //Hive表存储路径
    }

    @Data
    public static class HiveColumnInfo {
        private  String name;
        private  String type;
        private  String guid;
        private  String comment;
    }

    @Data
    @AllArgsConstructor
    public static class HiveClassificationInfo {
        private String typeName;
        private Set<String> superTypeNames;
        private Set<String> subTypeNames;
    }
}
