package com.webank.wedatasphere.dss.framework.admin.exception;

import com.webank.wedatasphere.dss.common.exception.DSSErrorException;


public class DSSAdminErrorException extends DSSErrorException {
    public DSSAdminErrorException(int errCode, String desc) {
        super(errCode, desc);
    }

    public DSSAdminErrorException(int errCode, String desc, String ip, int port, String serviceKind) {
        super(errCode, desc, ip, port, serviceKind);
    }
}
