package com.webank.wedatasphere.dss.datamodel.table.entity;


import lombok.Data;

import java.util.Date;

@Data
public class DssDatamodelTabelQuery {
    private Long id;

    private String dataBase;

    private String name;

    private String alias;

    private String creator;

    private String comment;

    private Date createTime;

    private Date updateTime;

    /**
     * 数仓层级
     */
    private String warehouseLayerName;

    /**
     * 数仓主题格式为： theme_domain_name.theme_name
     */
    private String warehouseThemeName;

    /**
     * 生命周期
     */
    private String lifecycle;

    private Integer isPartitionTable;

    private Integer isAvailable;

    /**
     * 存储类型：hive/mysql
     */
    private String storageType;

    /**
     * 授权的名字：userName、roleName
     */
    private String principalName;

    /**
     * 压缩格式
     */
    private String compress;

    /**
     * 文件格式
     */
    private String fileType;

    /**
     * 版本信息：默认1
     */
    private String version;

    /**
     * 是否外部表 0 内部表 1外部表
     */
    private Integer isExternal;

    /**
     * 外部表时 location
     */
    private String location;

    private Integer modelType;

    private String modelName;
}
