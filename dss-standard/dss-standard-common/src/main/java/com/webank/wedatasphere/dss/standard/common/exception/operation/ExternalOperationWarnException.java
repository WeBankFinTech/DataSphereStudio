package com.webank.wedatasphere.dss.standard.common.exception.operation;

import com.webank.wedatasphere.dss.standard.common.exception.AppStandardWarnException;

/**
 * @author enjoyyin
 * @date 2022-03-11
 * @since 0.5.0
 */
public class ExternalOperationWarnException extends AppStandardWarnException {

    public ExternalOperationWarnException(int errorCode, String message){
        super(errorCode, message);
    }

    public ExternalOperationWarnException(int errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }

}
