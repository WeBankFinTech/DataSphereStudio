package com.webank.wedatasphere.dss.data.governance.exception;


import com.webank.wedatasphere.linkis.common.exception.ErrorException;

public class DAOException extends ErrorException {

    public DAOException(int errCode, String desc) {
        super(errCode, desc);
    }

    public DAOException(int errCode, String desc, String ip, int port, String serviceKind) {
        super(errCode, desc, ip, port, serviceKind);
    }
}
