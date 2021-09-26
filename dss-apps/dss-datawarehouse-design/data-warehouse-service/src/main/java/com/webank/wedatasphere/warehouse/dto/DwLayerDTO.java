package com.webank.wedatasphere.warehouse.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@NoArgsConstructor
public class DwLayerDTO {
    private Long id;
    private String name;
    private String enName;
    private Boolean preset;
    private String description;
    private String principalName;
    private String dbs;
    private String owner;
    private Integer sort;
}
