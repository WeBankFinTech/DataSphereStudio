package com.webank.wedatasphere.dss.datamodel.table.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@ToString
public class DssDatamodelTableMaterializedHistory {
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 物化sql
     */
    private String materializedCode;

    /**
     * 物化原因
     */
    private String reason;

    /**
     * 物化者
     */
    private String creator;

    /**
     * succeed,failed,in progess
     */
    private Integer status;

    private Date createTime;

    private Date lastUpdateTime;

    private String taskId;

    private String errorMsg;

    /**
     * 表名
     */
    private String tablename;

    private String dataBase;

    /**
     * 版本信息：默认1
     */
    private String version;


}