package com.webank.wedatasphere.warehouse.exception;

import com.webank.wedatasphere.linkis.common.exception.ErrorException;

public class DwException extends ErrorException {

    public static final Integer ARGUMENT_ERROR = 21101;
    public static final Integer BUSINESS_ERROR = 22101;

    private int errorCode;
    private Object[] parameters;

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public Object[] getParameters() {
        return parameters;
    }

    public static DwException argumentReject(String errorMessage, Object... parameters) {
        return new DwException(ARGUMENT_ERROR, errorMessage, parameters);
    }

    public static DwException argumentReject(String errorMessage) {
        return new DwException(ARGUMENT_ERROR, errorMessage);
    }

    public static DwException stateReject(String errorMessage) {
        return new DwException(BUSINESS_ERROR, errorMessage);
    }

    public static DwException stateReject(String errorMessage, Object... parameters) {
        return new DwException(BUSINESS_ERROR, errorMessage, parameters);
    }

    public DwException(int errorCode, String message) {
        super(errorCode, message);
        this.errorCode = errorCode;
    }

    public DwException(int errorCode, String message, Object... parameters) {
        super(errorCode, message);
        this.errorCode = errorCode;
        this.parameters = parameters;
    }

    public DwException(int errorCode, String message, Throwable cause) {
        super(errorCode, message);
        this.errorCode = errorCode;
    }
}
