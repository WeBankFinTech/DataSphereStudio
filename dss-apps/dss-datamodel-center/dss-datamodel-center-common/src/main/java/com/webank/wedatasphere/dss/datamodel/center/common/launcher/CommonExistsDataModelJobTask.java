package com.webank.wedatasphere.dss.datamodel.center.common.launcher;


public class CommonExistsDataModelJobTask extends DataModelJobTask {

    @Override
    public void formatCode(String orgCode) {
        setCode(orgCode);
    }
}
