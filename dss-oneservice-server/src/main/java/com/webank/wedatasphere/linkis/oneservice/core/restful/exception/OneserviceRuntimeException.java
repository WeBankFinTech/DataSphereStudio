package com.webank.wedatasphere.linkis.oneservice.core.restful.exception;

/**
 * OneserviceRuntimeException
 *
 * @author lidongzhang
 */
public class OneserviceRuntimeException extends RuntimeException {

    public OneserviceRuntimeException(String message) {
        super(message);
    }

    public OneserviceRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
