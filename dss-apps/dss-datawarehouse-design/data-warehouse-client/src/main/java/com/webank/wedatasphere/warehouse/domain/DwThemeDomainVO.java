package com.webank.wedatasphere.warehouse.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Setter
@Getter
@ToString
public class DwThemeDomainVO {
    private Long id;
    private String name;
    private String enName;
    private String owner;
    private String principalName;
    private Integer sort;
    private String description;
    private Date createTime;
    private Date updateTime;
    private Boolean preset;
    private Boolean isAvailable;
}
