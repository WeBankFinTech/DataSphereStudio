package com.webank.wedatasphere.dss.datamodel.center.common.vo;


import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class PageVO {
    private Integer pageSize = 50;

    private Integer pageNum = 1;
}
