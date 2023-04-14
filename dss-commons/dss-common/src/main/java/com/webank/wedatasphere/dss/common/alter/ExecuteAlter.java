package com.webank.wedatasphere.dss.common.alter;

import com.webank.wedatasphere.dss.common.conf.AlterConfiguration;
import com.webank.wedatasphere.dss.common.entity.Alter;
import org.springframework.stereotype.Component;

@Component
public class ExecuteAlter {

    public void sendAlter(Alter alter) {
        ExceptionAlterSender alterSender = AlterConfiguration.getAlter();
        AlterContext alterContext = new AlterContext(alterSender);
        alterContext.executeStrategy(alter);
    }

}
