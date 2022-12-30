package com.webank.wedatasphere.dss.datamodel.center.common.ujes.task;


import com.webank.wedatasphere.dss.datamodel.center.common.ujes.DataModelUJESJobTaskBuilder;

public class DropTableDataModelUJESJobTask  extends DataModelUJESJobTask {

    public static DataModelUJESJobTaskBuilder newBuilder() {
        return new DataModelUJESJobTaskBuilder(new DropTableDataModelUJESJobTask());
    }

    DropTableDataModelUJESJobTask() {
    }


    @Override
    public void formatCode(String code) {
        setCode(String.format("drop table if exists %s",code));
    }

}