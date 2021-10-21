package com.webank.wedatasphere.dss.datamodel.center.common.ujes;


import lombok.Data;

@Data
public abstract class DataModelUJESJobTask {

    private String code;

    private Integer count = 10;

    abstract void formatCode(String code);
}
