package com.webank.wedatasphere.dss.datamodel.center.common.ujes.task;


import com.webank.wedatasphere.dss.datamodel.center.common.ujes.DataModelUJESJobTaskBuilder;

public class PreviewDataModelUJESJobTask  extends DataModelUJESJobTask {

    public static DataModelUJESJobTaskBuilder newBuilder() {
        return new DataModelUJESJobTaskBuilder(new PreviewDataModelUJESJobTask());
    }

    PreviewDataModelUJESJobTask() {
    }


    @Override
    public void formatCode(String code) {
        setCode(String.format("select * from %s limit %s", code,getCount()+""));
    }

}