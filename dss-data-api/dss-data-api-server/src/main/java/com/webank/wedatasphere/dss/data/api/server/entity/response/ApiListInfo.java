package com.webank.wedatasphere.dss.data.api.server.entity.response;

import lombok.Data;

@Data
public class ApiListInfo {
    private int id;
    private String name;
    private String path;
    private String reqFields;
}
