package com.webank.wedatasphere.dss.data.api.server.entity.request;

import lombok.Data;

@Data
public class ReqField {
    private String name;
    private String type;
    private String compare;
    private String comment;

}
