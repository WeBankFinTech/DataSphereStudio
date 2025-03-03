package com.webank.wedatasphere.dss.git.common.protocol.exception;

import com.webank.wedatasphere.dss.common.exception.DSSErrorException;

public class GitErrorException extends DSSErrorException {

    public GitErrorException(int errCode, String desc) {
        super(errCode, desc);
    }

    public GitErrorException(int errCode, String desc, Throwable cause) {
        super(errCode, desc);
        initCause(cause);
    }
}
