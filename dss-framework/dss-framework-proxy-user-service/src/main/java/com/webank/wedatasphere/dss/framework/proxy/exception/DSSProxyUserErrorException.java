package com.webank.wedatasphere.dss.framework.proxy.exception;

import com.webank.wedatasphere.dss.common.exception.DSSErrorException;


public class DSSProxyUserErrorException extends DSSErrorException {

    public DSSProxyUserErrorException(int errCode, String desc) {
        super(errCode, desc);
    }

    public DSSProxyUserErrorException(int errCode, String desc, String ip, int port, String serviceKind) {
        super(errCode, desc, ip, port, serviceKind);
    }

}
