package com.webank.wedatasphere.dss.framework.workspace.bean.itsm;

public class ItsmResponse {

    private String data;
    private int retCode;
    private String retDetail;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public ItsmResponse data(String data) {
        this.data = data;
        return this;
    }

    public int getRetCode() {
        return retCode;
    }

    public void setRetCode(int retCode) {
        this.retCode = retCode;
    }

    public ItsmResponse retCode(int retCode) {
        this.retCode = retCode;
        return this;
    }

    public String getRetDetail() {
        return retDetail;
    }

    public void setRetDetail(String retDetail) {
        this.retDetail = retDetail;
    }

    public ItsmResponse retDetail(String retDetail) {
        this.retDetail = retDetail;
        return this;
    }

    public static ItsmResponse ok(){
        ItsmResponse itsmResponse = new ItsmResponse();
        return itsmResponse.retCode(0);
    }
    public static ItsmResponse error(){
        ItsmResponse itsmResponse = new ItsmResponse();
        return itsmResponse.retCode(-1);
    }


}
