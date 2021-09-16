package com.webank.wedatasphere.dss.datamodel.center.common.exception;

import com.webank.wedatasphere.linkis.common.exception.ErrorException;

/**
 * @author helong
 * @date 2021/9/16
 */
public class DSSDatamodelCenterException extends ErrorException {

    public DSSDatamodelCenterException(int errCode, String desc) {
        super(errCode, desc);
    }

    public DSSDatamodelCenterException(int errCode, String desc, String ip, int port, String serviceKind) {
        super(errCode, desc, ip, port, serviceKind);
    }
}
