package com.webank.wedatasphere.warehouse.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Setter
@Getter
@ToString
@NoArgsConstructor
public class DwModifierDTO {
    private Long id;
    private String modifierType;
    private String modifierTypeEn;
    private Long layerId;
    private String layerArea;
    private Long themeDomainId;
    private String themeArea;
    private String description;
    private List<DwModifierListDTO> list;
    private boolean referenced;
}
