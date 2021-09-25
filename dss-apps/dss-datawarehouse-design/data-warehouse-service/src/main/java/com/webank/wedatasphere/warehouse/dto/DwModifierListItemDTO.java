package com.webank.wedatasphere.warehouse.dto;

import lombok.*;

import java.util.Date;
import java.util.List;

@Data
public class DwModifierListItemDTO {
    private Long id;
    private String modifierType;
    private String layerArea;
    private String themeArea;
    private String description;
    private Boolean isAvailable;
    private Date createTime;
    private Date updateTime;
}
