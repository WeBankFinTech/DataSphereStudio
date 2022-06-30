package com.webank.wedatasphere.dss.framework.admin.common.domain;

//import lombok.AllArgsConstructor;
//import lombok.Getter;
//import lombok.ToString;

//@Getter
//@ToString
//@AllArgsConstructor
public enum ResponseEnum {
    /*
        case -1 => 401
                case 0 => 200
                case 1 => 400
                case 2 => 412
                case 3 => 403
                case 4 => 206*/
    //-1 no login, 0 success, 1 error, 2 validate failed, 3 auth failed, 4 warning
    NO_LOGIN(-1, "no login"),
    SUCCESS(0, "success"),
    ERROR(1, "error"),
    VALIDATE_FAILED(2, "validate failed"),
    AUTH_FAILED(3, "auth failed"),
    WARNING(4,"warning");

    ResponseEnum(Integer status, String message) {
        this.status = status;
        this.message = message;
    }

    @Override
    public String toString() {
        return "ResponseEnum{" +
                "status=" + status +
                ", message='" + message + '\'' +
                '}';
    }

    private Integer status;
    private String  message;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


}
