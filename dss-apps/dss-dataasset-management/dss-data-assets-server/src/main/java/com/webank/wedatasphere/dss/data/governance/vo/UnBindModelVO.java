package com.webank.wedatasphere.dss.data.governance.vo;

import lombok.Data;

@Data
public class UnBindModelVO {
    private String modelName;

    private String modelType;

    private String tableName;

    private String guid;
}
