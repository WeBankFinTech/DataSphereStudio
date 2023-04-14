package com.webank.wedatasphere.dss.common.alter;

import com.webank.wedatasphere.dss.common.entity.Alter;

public class AlterContext {

    private final ExceptionAlterSender exceptionAlterSender;

    public AlterContext(ExceptionAlterSender exceptionAlterSender) {
        this.exceptionAlterSender = exceptionAlterSender;
    }

    public void executeStrategy(Alter alter) {
        exceptionAlterSender.sendAlter(alter);
    }
}
