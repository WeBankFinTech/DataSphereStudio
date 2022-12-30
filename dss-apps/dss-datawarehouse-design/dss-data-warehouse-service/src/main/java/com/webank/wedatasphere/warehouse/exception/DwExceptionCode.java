package com.webank.wedatasphere.warehouse.exception;

// 41000 ~ 41999
public enum DwExceptionCode {

    CREATE_MODEL_TYPE_ERROR(41001),
    DELETE_MODEL_TYPE_ERROR(41002),
    UPDATE_MODEL_TYPE_ERROR(41003),
    GET_AVAILABLE_DBS_ERROR(41004),
    GET_PRINCIPAL_USERS_ERROR(41005),
    GET_PRINCIPAL_ROLES_ERROR(41006),
    ;

    private int code;

    DwExceptionCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
