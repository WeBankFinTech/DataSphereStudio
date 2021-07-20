package com.webank.wedatasphere.dss.framework.dbapi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
public class ApiGroup {
    private Integer id;
    private String name;
    private String note;
    private int workspaceId;
    private String createBy;

}
