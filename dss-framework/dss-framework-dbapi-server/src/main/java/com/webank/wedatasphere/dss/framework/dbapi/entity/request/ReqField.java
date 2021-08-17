package com.webank.wedatasphere.dss.framework.dbapi.entity.request;

import lombok.Data;

@Data
public class ReqField {
    private String name;
    private String type;
    private String compare;
    private String comment;

}
