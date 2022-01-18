package com.webank.wedatasphere.dss.datamodel.table.vo;


import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class TableQueryOneVO {

    private String name;

    /**
     * atlas标识
     */
    private String guid;


    private String user;

}
