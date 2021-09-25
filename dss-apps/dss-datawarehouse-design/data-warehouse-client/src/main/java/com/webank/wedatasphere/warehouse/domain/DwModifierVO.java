package com.webank.wedatasphere.warehouse.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Setter
@Getter
@ToString
public class DwModifierVO {
    private Long id;
    private String modifierType;
    private String layerArea;
    private String themeArea;
    private String description;
    private Boolean isAvailable;
    private Date createTime;
    private Date updateTime;
}
