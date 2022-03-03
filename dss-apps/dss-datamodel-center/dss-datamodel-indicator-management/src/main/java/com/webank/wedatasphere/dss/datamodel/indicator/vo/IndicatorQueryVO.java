package com.webank.wedatasphere.dss.datamodel.indicator.vo;

import com.webank.wedatasphere.dss.datamodel.center.common.vo.PageVO;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class IndicatorQueryVO extends PageVO {

    private String name;

    private Integer indicatorType;

    private Integer isAvailable;

    private String owner;

    private String warehouseThemeName;

}
