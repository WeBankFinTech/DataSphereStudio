package com.webank.wedatasphere.dss.datamodel.measure.vo;

import com.webank.wedatasphere.dss.datamodel.center.common.vo.PageVO;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class MeasureQueryVO extends PageVO {

    private String name;

    private Integer isAvailable;

    private String owner;

    private String warehouseThemeName;

}
