package com.webank.wedatasphere.dss.common.alter;

import com.webank.wedatasphere.dss.common.entity.Alter;

public interface ExceptionAlterSender {
    /**
     * send alter to user when server exception,
     */
    void sendAlter(Alter alter);
}
