package com.webank.wedatasphere.warehouse.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@NoArgsConstructor
public class DwThemeDomainDTO {
    private Long id;
    private String name;
    private String enName;
    private String owner;
    private String principalName;
    private Integer sort;
    private String description;
    private boolean referenced;
}
