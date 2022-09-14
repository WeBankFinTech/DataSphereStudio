package com.webank.wedatasphere.dss.datamodel.center.common.launcher;


public class DataExistsDataModelJobTask extends DataModelJobTask {
    @Override
    public void formatCode(String orgCode) {
        setCode(String.format("select * from %s limit 1",orgCode));
    }

}
