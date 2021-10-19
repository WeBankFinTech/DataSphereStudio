package com.webank.wedatasphere.dss.datamodel.center.common.launcher;


public class CommonExistsExchangisJobTask extends ExchangisJobTask{

    @Override
    public void formatCode(String orgCode) {
        setCode(orgCode);
    }
}
