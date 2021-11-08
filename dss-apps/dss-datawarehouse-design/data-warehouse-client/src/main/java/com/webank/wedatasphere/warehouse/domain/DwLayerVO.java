package com.webank.wedatasphere.warehouse.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class DwLayerVO {
    private String id;
    private String name;
    private String enName;
    private Boolean preset;
    private String description;
    private String principalName;
    private String dbs;
    private Integer sort;
    private Boolean isAvailable;
    private Long createTime;
    private Long updateTime;
}
