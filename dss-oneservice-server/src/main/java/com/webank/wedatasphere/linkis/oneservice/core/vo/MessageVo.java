package com.webank.wedatasphere.linkis.oneservice.core.vo;

/**
 * MessageVo
 *
 * @author lidongzhang
 */
public class MessageVo {
    private Integer status = 0;

    private String message = "OK";

    private Object data;

    public MessageVo() {
    }

    public MessageVo(Integer status, String message, Object data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public Integer getStatus() {
        return status;
    }

    public MessageVo setStatus(Integer status) {
        this.status = status;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public MessageVo setMessage(String message) {
        this.message = message;
        return this;
    }

    public Object getData() {
        return data;
    }

    public MessageVo setData(Object data) {
        this.data = data;
        return this;
    }
}
