package com.webank.wedatasphere.warehouse.cqe;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class DwLayerUpdateCommand {
    private Long id;
    private String name;
    private String enName;
    private String databases;
    private Integer order;
//    private String autoCollectStrategy;
    private String owner;
    private String description;
    private String principalName;
}
