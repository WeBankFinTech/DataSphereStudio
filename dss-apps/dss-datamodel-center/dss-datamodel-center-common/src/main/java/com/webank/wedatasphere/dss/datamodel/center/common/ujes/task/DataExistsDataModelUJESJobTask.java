package com.webank.wedatasphere.dss.datamodel.center.common.ujes.task;


import com.webank.wedatasphere.dss.datamodel.center.common.ujes.DataModelUJESJobTaskBuilder;

public class DataExistsDataModelUJESJobTask extends DataModelUJESJobTask{

    public static DataModelUJESJobTaskBuilder newBuilder(){
        return new DataModelUJESJobTaskBuilder(new DataExistsDataModelUJESJobTask());
    }

    DataExistsDataModelUJESJobTask(){};

    @Override
    public void formatCode(String code) {
        setCode( String.format("select * from %s limit 1",code));
    }
}
