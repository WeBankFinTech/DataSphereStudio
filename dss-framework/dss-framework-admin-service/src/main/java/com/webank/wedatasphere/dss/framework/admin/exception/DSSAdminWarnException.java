package com.webank.wedatasphere.dss.framework.admin.exception;

import com.webank.wedatasphere.dss.common.exception.DSSRuntimeException;


public class DSSAdminWarnException extends DSSRuntimeException {

    public DSSAdminWarnException(int errCode, String desc) {
        super(errCode, desc);
    }

    public DSSAdminWarnException(String desc) {
        this(50000, desc);
    }
}
