package com.webank.wedatasphere.dss.datamodel.center.common.constant;


public enum ColumnType {
    COLUMN(0),
    PARTITION_KEY(1);
    private Integer code;

    ColumnType(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
}
