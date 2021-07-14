package com.webank.wedatasphere.dss.framework.dbapi.entity.request;

import lombok.Data;

@Data
public class ApiConfigRequest {
    private String apiType;
    private String apiPath;
    private String apiName;
    private String protocol;
    private String scope;
    private String method;
    private String describe;
    private String datasourceType;
    private String datasourceId;
    private String tableName;
    private String memory;
    private String RequestTimeout;
    private String label;
    private String requestDesc;
    private String responseDesc;
    private String orderDesc;
    private String sql;
    private String responseType;

}






