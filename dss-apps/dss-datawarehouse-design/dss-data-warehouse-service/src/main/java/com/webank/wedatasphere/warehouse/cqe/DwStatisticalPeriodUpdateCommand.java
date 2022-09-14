package com.webank.wedatasphere.warehouse.cqe;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class DwStatisticalPeriodUpdateCommand {
    private Long id;
    private Long themeDomainId;
    private Long layerId;
    private String name;
    private String enName;
    private String description;
    private String statStartFormula;
    private String statEndFormula;
    private String principalName;
    private String owner;
}
