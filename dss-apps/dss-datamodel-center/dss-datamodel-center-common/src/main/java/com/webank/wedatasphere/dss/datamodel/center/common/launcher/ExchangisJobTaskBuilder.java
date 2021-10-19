package com.webank.wedatasphere.dss.datamodel.center.common.launcher;


public class ExchangisJobTaskBuilder {

    private ExchangisJobTask exchangisJobTask;

    public ExchangisJobTaskBuilder withDataExistsExchangisJobTask(){
        exchangisJobTask = new DataExistsExchangisJobTask();
        return this;
    }

    public ExchangisJobTaskBuilder withCommonExchangisJobTask(){
        exchangisJobTask = new CommonExistsExchangisJobTask();
        return this;
    }

    public ExchangisJobTaskBuilder creator(String creator){
        exchangisJobTask.setCreator(creator);
        return this;
    }

    public ExchangisJobTaskBuilder engineType(String engineType){
        exchangisJobTask.setEngineType(engineType);
        return this;
    }

    public ExchangisJobTaskBuilder runType(String runType){
        exchangisJobTask.setRunType(runType);
        return this;
    }

    public ExchangisJobTaskBuilder executeUser(String executeUser){
        exchangisJobTask.setExecuteUser(executeUser);
        return this;
    }

    public ExchangisJobTaskBuilder code(String code){
        exchangisJobTask.formatCode(code);
        return this;
    }

    public ExchangisJobTask build(){
        return exchangisJobTask;
    }
}
