package com.webank.wedatasphere.dss.datamodel.dimension.vo;

import com.webank.wedatasphere.dss.datamodel.center.common.vo.PageVO;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class DimensionQueryVO extends PageVO {

    private String name;

    private Integer isAvailable;

    private String owner;

    private String warehouseThemeName;

}
