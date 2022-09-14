package com.webank.wedatasphere.dss.datamodel.center.common.exception;

import org.apache.linkis.common.exception.ErrorException;


public class DSSDatamodelCenterException extends ErrorException {

    public DSSDatamodelCenterException(int errCode, String desc) {
        super(errCode, desc);
    }

    public DSSDatamodelCenterException(int errCode, String desc, String ip, int port, String serviceKind) {
        super(errCode, desc, ip, port, serviceKind);
    }
}
