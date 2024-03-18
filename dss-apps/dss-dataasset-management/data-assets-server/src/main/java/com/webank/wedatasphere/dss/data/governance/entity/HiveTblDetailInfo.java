package com.webank.wedatasphere.dss.data.governance.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Set;


@Data
public class HiveTblDetailInfo implements Serializable {
    private HiveTblBasicInfo basic;
    private List<HiveColumnInfo> columns;
    private List<HiveColumnInfo> partitionKeys;


    @Data
    public static class HiveTblBasicInfo extends HiveTblSimpleInfo {
        private  String store;
        private  String comment;
        private Set<String> labels;
        private Boolean isParTbl;
    }

    @Data
    public static class HiveColumnInfo {
        private  String name;
        private  String type;
        private  String guid;
        private  String comment;
    }
}
