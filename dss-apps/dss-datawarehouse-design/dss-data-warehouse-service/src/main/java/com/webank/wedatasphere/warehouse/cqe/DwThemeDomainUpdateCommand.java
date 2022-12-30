package com.webank.wedatasphere.warehouse.cqe;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class DwThemeDomainUpdateCommand {
    private Long id;
    private String name;
    private String enName;
    private String owner;
    private String principalName;
    private String description;
    private Integer sort;
//    private String authority;
}
