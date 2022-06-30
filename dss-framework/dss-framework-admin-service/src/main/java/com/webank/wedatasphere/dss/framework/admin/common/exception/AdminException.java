package com.webank.wedatasphere.dss.framework.admin.common.exception;


import com.webank.wedatasphere.dss.framework.admin.common.domain.ResponseEnum;
//import lombok.Data;
//import lombok.NoArgsConstructor;

//@Data
//@NoArgsConstructor
public class AdminException extends RuntimeException {

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * 错误码
     */
    private Integer code;
    /**
     * 错误信息
     */
    private String message;

    /**
     * @param message 错误消息
     */
    public AdminException(String message) {
        this.message = message;
    }

    /**
     * @param message 错误消息
     * @param code    错误码
     */
    public AdminException(String message, Integer code) {
        this.message = message;
        this.code = code;
    }

    /**
     * @param message 错误消息
     * @param code    错误码
     * @param cause   原始异常对象
     */
    public AdminException(String message, Integer code, Throwable cause) {
        super(cause);
        this.message = message;
        this.code = code;
    }

    /**
     * @param resultCodeEnum 接收枚举类型
     */
    public AdminException(ResponseEnum resultCodeEnum) {
        this.message = resultCodeEnum.getMessage();
        this.code = resultCodeEnum.getStatus();
    }

    /**
     * @param resultCodeEnum 接收枚举类型
     * @param cause          原始异常对象
     */
    public AdminException(ResponseEnum resultCodeEnum, Throwable cause) {
        super(cause);
        this.message = resultCodeEnum.getMessage();
        this.code = resultCodeEnum.getStatus();
    }
}
