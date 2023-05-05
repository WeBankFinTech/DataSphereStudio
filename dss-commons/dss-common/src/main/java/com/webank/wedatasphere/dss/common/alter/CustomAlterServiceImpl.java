package com.webank.wedatasphere.dss.common.alter;

import com.webank.wedatasphere.dss.common.entity.Alter;

public class CustomAlterServiceImpl implements ExceptionAlterSender {

    @Override
    public void sendAlter(Alter alter) {
        // Users can customize their own alarm methods
    }
}
