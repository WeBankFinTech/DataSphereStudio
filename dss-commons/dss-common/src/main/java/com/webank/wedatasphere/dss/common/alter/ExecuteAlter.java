package com.webank.wedatasphere.dss.common.alter;

import com.webank.wedatasphere.dss.common.conf.AlterConfiguration;
import com.webank.wedatasphere.dss.common.entity.Alter;

public class ExecuteAlter {

    public void sendAlter(String alterType, Alter alter) throws Exception {
        ExceptionAlterSender alterSender = AlterConfiguration.getAlter();
        AlterContext alterContext = new AlterContext(alterSender);
        alterContext.executeStrategy(alterType, alter);
    }

}
