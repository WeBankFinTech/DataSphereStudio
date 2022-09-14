package com.webank.wedatasphere.dss.datamodel.table.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Set;


@Data
public class HiveTblDetailInfoDTO implements Serializable {
    private HiveTblBasicInfo basic;
    private List<HiveColumnInfoDTO> columns;
    private List<HiveColumnInfoDTO> partitionKeys;


    @Data
    public static class HiveTblBasicInfo extends HiveTblSimpleInfo {
        private  String store;
        private  String comment;
        private Set<String> labels;
        private Boolean isParTbl;
    }

    @Data
    public static class HiveColumnInfoDTO {
        private  String name;
        private  String type;
        private  String guid;
        private  String comment;
    }

}
