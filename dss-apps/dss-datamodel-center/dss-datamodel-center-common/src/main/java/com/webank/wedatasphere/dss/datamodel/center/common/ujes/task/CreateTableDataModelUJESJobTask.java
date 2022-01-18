package com.webank.wedatasphere.dss.datamodel.center.common.ujes.task;


import com.webank.wedatasphere.dss.datamodel.center.common.ujes.DataModelUJESJobTaskBuilder;

public class CreateTableDataModelUJESJobTask extends DataModelUJESJobTask {

    public static DataModelUJESJobTaskBuilder newBuilder() {
        return new DataModelUJESJobTaskBuilder(new CreateTableDataModelUJESJobTask());
    }

    CreateTableDataModelUJESJobTask() {
    }


    @Override
    public void formatCode(String code) {
        setCode(code);
    }
}