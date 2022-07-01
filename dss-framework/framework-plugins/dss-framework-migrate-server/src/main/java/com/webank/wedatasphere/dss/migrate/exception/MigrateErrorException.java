package com.webank.wedatasphere.dss.migrate.exception;

import org.apache.linkis.common.exception.ErrorException;

public class MigrateErrorException extends ErrorException {


    public MigrateErrorException(int errCode, String desc) {
        super(errCode, desc);
    }

    public MigrateErrorException(int errCode, String desc, Throwable throwable){
        this(errCode, desc);
        this.initCause(throwable);
    }

}
