package com.webank.wedatasphere.dss.datamodel.center.common.ujes;


public class DataModelUJESJobTaskBuilder {

    private DataModelUJESJobTask dataModelUJESJobTask;

    DataModelUJESJobTaskBuilder(DataModelUJESJobTask dataModelUJESJobTask){
        this.dataModelUJESJobTask = dataModelUJESJobTask;
    }

    public DataModelUJESJobTaskBuilder code(String code){
        dataModelUJESJobTask.formatCode(code);
        return this;
    }

    public DataModelUJESJobTaskBuilder count(Integer count){
        dataModelUJESJobTask.setCount(count);
        return this;
    }

    public DataModelUJESJobTask build(){
        return dataModelUJESJobTask;
    }
}
