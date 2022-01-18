package com.webank.wedatasphere.dss.datamodel.center.common.ujes;


import com.webank.wedatasphere.dss.datamodel.center.common.ujes.task.DataModelUJESJobTask;

public class DataModelUJESJobTaskBuilder {

    private DataModelUJESJobTask dataModelUJESJobTask;

    public DataModelUJESJobTaskBuilder(DataModelUJESJobTask dataModelUJESJobTask){
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

    public DataModelUJESJobTaskBuilder user(String user){
        dataModelUJESJobTask.setUser(user);
        return this;
    }

    public DataModelUJESJobTask build(){
        return dataModelUJESJobTask;
    }
}
