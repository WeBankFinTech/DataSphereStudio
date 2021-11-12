package com.webank.wedatasphere.dss.datamodel.table.dto;


import com.google.common.collect.Lists;
import lombok.Data;
import lombok.ToString;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
@Data
@ToString
public class TableQueryDTO {

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

    private List<TableColumnQueryDTO> columns = Lists.newArrayList();

    /**
     * 标签
     */
    private String label;


    private TableStatsDTO stats = new TableStatsDTO();


    private TableHeadlineDTO headline;

    public static TableQueryDTO toTableStatsDTO(HiveTblDetailInfoDTO hiveTblDTO,String name){
        TableQueryDTO dto = new TableQueryDTO();
        dto.setName(name);
        dto.setCreateTime(hiveTblDTO.getBasic().getCreateTime());
        dto.setCreator(hiveTblDTO.getBasic().getOwner());
        dto.setComment(hiveTblDTO.getBasic().getComment());
        if (!CollectionUtils.isEmpty(hiveTblDTO.getBasic().getLabels())){
            StringBuilder sb = new StringBuilder();
            for (String label:hiveTblDTO.getBasic().getLabels()){
                sb.append(label).append(";");
            }
           dto.setLabel(StringUtils.substringBeforeLast(sb.toString(),";"));
        }
        dto.setIsPartitionTable(hiveTblDTO.getBasic().getIsParTbl()?0:1);

        if (!CollectionUtils.isEmpty(hiveTblDTO.getColumns())){
            hiveTblDTO.getColumns().forEach(hiveColumn->{
                TableColumnQueryDTO columnQueryDTO = new TableColumnQueryDTO();
                columnQueryDTO.setComment(hiveColumn.getComment());
                columnQueryDTO.setName(hiveColumn.getName());
                columnQueryDTO.setType(hiveColumn.getType());
                columnQueryDTO.setIsPartitionField(1);
                dto.columns.add(columnQueryDTO);
            });
        }

        if (!CollectionUtils.isEmpty(hiveTblDTO.getPartitionKeys())){
            hiveTblDTO.getPartitionKeys().forEach(hiveColumn->{
                TableColumnQueryDTO columnQueryDTO = new TableColumnQueryDTO();
                columnQueryDTO.setComment(hiveColumn.getComment());
                columnQueryDTO.setName(hiveColumn.getName());
                columnQueryDTO.setType(hiveColumn.getType());
                columnQueryDTO.setIsPartitionField(0);
                dto.columns.add(columnQueryDTO);
            });
        }
        TableHeadlineDTO headlineDTO = new TableHeadlineDTO();
        headlineDTO.setStorageType(0);
        headlineDTO.setTableType(0);
        headlineDTO.setEntityType(1);
        dto.setHeadline(headlineDTO);
        return dto;
    }
}
