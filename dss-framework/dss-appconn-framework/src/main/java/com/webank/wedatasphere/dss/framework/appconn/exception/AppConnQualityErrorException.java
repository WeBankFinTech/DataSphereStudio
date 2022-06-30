package com.webank.wedatasphere.dss.framework.appconn.exception;

import com.webank.wedatasphere.dss.common.exception.DSSErrorException;

/**
 * @author enjoyyin
 * @date 2022-04-14
 * @since 0.5.0
 */
public class AppConnQualityErrorException extends DSSErrorException {

    public AppConnQualityErrorException(int errCode, String desc) {
        super(errCode, desc);
    }

    public AppConnQualityErrorException(int errCode, String desc, Throwable cause) {
        super(errCode, desc);
        initCause(cause);
    }

}
