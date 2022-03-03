package com.webank.wedatasphere.warehouse.cqe;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class DwLayerCreateCommand {
    private String name;
    private String enName;
    private String owner;
    private String principalName;
    private String databases;
    private String description;
    private Integer order;
//    private String autoCollectStrategy;
}
