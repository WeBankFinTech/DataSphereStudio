package com.webank.wedatasphere.dss.datamodel.center.common.launcher;



public abstract class ExchangisJobTask {

     private String engineType;

     private String runType;

     private String code;

     private String creator;

     private String executeUser;

     public void setEngineType(String engineType) {
          this.engineType = engineType;
     }

     public void setRunType(String runType) {
          this.runType = runType;
     }

     public void setCode(String code) {
          this.code = code;
     }

     public void setCreator(String creator) {
          this.creator = creator;
     }

     public void setExecuteUser(String executeUser) {
          this.executeUser = executeUser;
     }

     public String getEngineType() {
          return engineType;
     }

     public String getRunType() {
          return runType;
     }

     public String getCode() {
          return code;
     }


     public String getCreator() {
          return creator;
     }


     public String getExecuteUser() {
          return executeUser;
     }



     public abstract void formatCode(String orgCode);
}
