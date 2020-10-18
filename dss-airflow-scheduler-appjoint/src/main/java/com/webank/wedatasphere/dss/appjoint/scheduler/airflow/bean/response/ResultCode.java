package com.webank.wedatasphere.dss.appjoint.scheduler.airflow.bean.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResultCode {
    // 200
    SUCCESS("success"),

    // 400
    REQUEST_DATA_INVALID("request_data_invalid"),

    ILLEGAL_OPERATION("illegal_operation"),

    // 403
    VERIFY_TOKEN_FAILED("verify_token_failed"),

    // 404
    RESOURCE_NOT_FOUND("resource_not_found"),

    // 5xx
    NULL_RESPONSE("null_response"),

    REQUEST_FORBIDDEN("request_forbidden"),

    INTERNAL_ERROR("internal_error"),

    MISS_PARAM("miss_param"),

    PERMISSION_DENIED("permission_denied"),

    INVALID_PARAM("invalid_param"),

    FAIL("fail"),

    NETWORK_ERROR("network_error"),

    UNDEFINED_ACTION("undefined_action");

    private final String resultCode;
}
