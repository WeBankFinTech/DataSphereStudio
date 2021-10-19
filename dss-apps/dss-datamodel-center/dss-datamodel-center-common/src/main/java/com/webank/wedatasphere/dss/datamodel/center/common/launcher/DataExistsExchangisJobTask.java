package com.webank.wedatasphere.dss.datamodel.center.common.launcher;


public class DataExistsExchangisJobTask extends ExchangisJobTask{
    @Override
    public void formatCode(String orgCode) {
        setCode(String.format("select * from %s limit 1",orgCode));
    }

}
