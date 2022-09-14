package com.webank.wedatasphere.dss.data.governance.entity;


import lombok.Data;

@Data
public class HiveTblStatsDTO {

    private Integer columnCount = 0;

    private Long totalSize = 0L;

    private Integer numFiles = 0;

    private Integer partitionCount = 0;

    private Integer accessCount = 0;
}
