package com.webank.wedatasphere.dss.datamodel.table.vo;


import lombok.Data;
import lombok.ToString;

import java.util.Date;
import java.util.List;

@Data
@ToString
public class TableVersionAddVO {
    private String dataBase;

    private String name;

    private String alias;

    private String creator;

    private String comment;

    /**
     * 数仓层级
     */
    private String warehouseLayerName;

    /**
     * 数仓层级英文
     */
    private String warehouseLayerNameEn;

    /**
     * 数仓主题格式为： theme_domain_name.theme_name
     */
    private String warehouseThemeName;

    /**
     * 数仓主题英文
     */
    private String warehouseThemeNameEn;

    /**
     * 生命周期
     */
    private String lifecycle;

    private String lifecycleEn;

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
     * 是否外部表 0 内部表 1外部表
     */
    private Integer isExternal;

    /**
     * 外部表时 location
     */
    private String location;

    private String label;

    private List<TableColumnVO> columns;


}
