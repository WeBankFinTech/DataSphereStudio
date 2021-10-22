package com.webank.wedatasphere.dss.datamodel.center.common.ujes.task;


import lombok.Data;

@Data
public abstract class DataModelUJESJobTask {

    private String code;

    private Integer count = 10;

    public abstract void formatCode(String code);
}
