package com.webank.wedatasphere.dss.data.governance.vo;


import lombok.Data;

@Data
public class UnBindLabelVO {
    private String label;

    private String tableName;

    private String labelGuid;

    private String tableGuid;

    private String relationGuid;
}
