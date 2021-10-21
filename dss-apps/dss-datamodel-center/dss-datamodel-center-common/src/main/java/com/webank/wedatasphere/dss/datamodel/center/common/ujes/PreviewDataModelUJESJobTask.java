package com.webank.wedatasphere.dss.datamodel.center.common.ujes;


public class PreviewDataModelUJESJobTask  extends DataModelUJESJobTask {

    public static DataModelUJESJobTaskBuilder newBuilder() {
        return new DataModelUJESJobTaskBuilder(new PreviewDataModelUJESJobTask());
    }

    PreviewDataModelUJESJobTask() {
    }


    @Override
    void formatCode(String code) {
        setCode(String.format("select * from %s limit %s", code,getCount()+""));
    }

}