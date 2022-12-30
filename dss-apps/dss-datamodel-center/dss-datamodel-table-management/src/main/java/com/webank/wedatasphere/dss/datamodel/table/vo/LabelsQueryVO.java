package com.webank.wedatasphere.dss.datamodel.table.vo;

import com.webank.wedatasphere.dss.datamodel.center.common.vo.PageVO;
import lombok.Data;

@Data
public class LabelsQueryVO extends PageVO {
    private String name;

    private Integer isAvailable;

    private String owner;

    private String warehouseThemeName;
}
