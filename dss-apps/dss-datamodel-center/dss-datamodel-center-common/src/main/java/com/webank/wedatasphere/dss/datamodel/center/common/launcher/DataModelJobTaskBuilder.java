package com.webank.wedatasphere.dss.datamodel.center.common.launcher;


public class DataModelJobTaskBuilder {

    private DataModelJobTask dataModelJobTask;

    public DataModelJobTaskBuilder withDataExistsExchangisJobTask(){
        dataModelJobTask = new DataExistsDataModelJobTask();
        return this;
    }

    public DataModelJobTaskBuilder withCommonExchangisJobTask(){
        dataModelJobTask = new CommonExistsDataModelJobTask();
        return this;
    }

    public DataModelJobTaskBuilder creator(String creator){
        dataModelJobTask.setCreator(creator);
        return this;
    }

    public DataModelJobTaskBuilder engineType(String engineType){
        dataModelJobTask.setEngineType(engineType);
        return this;
    }

    public DataModelJobTaskBuilder runType(String runType){
        dataModelJobTask.setRunType(runType);
        return this;
    }

    public DataModelJobTaskBuilder executeUser(String executeUser){
        dataModelJobTask.setExecuteUser(executeUser);
        return this;
    }

    public DataModelJobTaskBuilder code(String code){
        dataModelJobTask.formatCode(code);
        return this;
    }

    public DataModelJobTask build(){
        return dataModelJobTask;
    }
}
