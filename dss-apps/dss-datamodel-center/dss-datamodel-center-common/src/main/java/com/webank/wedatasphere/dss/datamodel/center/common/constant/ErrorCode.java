package com.webank.wedatasphere.dss.datamodel.center.common.constant;


public enum ErrorCode {

    INDICATOR_VERSION_ADD_ERROR(212031),
    INDICATOR_VERSION_ROLL_BACK_ERROR(212032),

    MEASURE_QUERY_ERROR(212101),

    DIMENSION_QUERY_ERROR(212201),

    INDICATOR_ADD_ERROR(212001),
    INDICATOR_QUERY_ERROR(212002),
    INDICATOR_UPDATE_ERROR(212003),
    INDICATOR_CONTENT_ADD_ERROR(212011),


    TABLE_ADD_ERROR(212301),
    TABLE_UPDATE_ERROR(212302);




    private int code;

    ErrorCode(int i) {
        code = i;
    }

    public int getCode() {
        return code;
    }
}
