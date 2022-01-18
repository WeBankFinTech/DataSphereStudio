package com.webank.wedatasphere.dss.datamodel.table.dto;


import com.google.common.collect.Lists;
import com.webank.wedatasphere.dss.data.governance.entity.HiveTblStatsDTO;
import com.webank.wedatasphere.dss.datamodel.center.common.constant.ColumnType;
import com.webank.wedatasphere.dss.datamodel.center.common.constant.LabelConstant;
import com.webank.wedatasphere.dss.datamodel.center.common.constant.TabelExternalType;
import lombok.Data;
import lombok.ToString;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.Optional;

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

    private Date lastAccessTime;

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

    public static TableQueryDTO toTableStatsDTO(HiveTblDetailInfoDTO hiveTblDTO, HiveTblStatsDTO hiveTblStatsDTO, String name){
        TableQueryDTO dto = new TableQueryDTO();
        dto.setName(name);
        dto.setCreateTime(hiveTblDTO.getBasic().getCreateTime());
        dto.setLastAccessTime(hiveTblDTO.getBasic().getLastAccessTime());
        dto.setCreator(hiveTblDTO.getBasic().getOwner());
        dto.setComment(hiveTblDTO.getBasic().getComment());

        Optional<TabelExternalType> tableExternalType = TabelExternalType.getByType(hiveTblDTO.getBasic().getExternal());
        tableExternalType.ifPresent(e -> dto.setIsExternal(e.getCode()));

        if (!CollectionUtils.isEmpty(hiveTblDTO.getBasic().getLabels())){
            StringBuilder sb = new StringBuilder();
            for (String label:hiveTblDTO.getBasic().getLabels()){
                sb.append(label).append(LabelConstant.SEPARATOR);
            }
           dto.setLabel(StringUtils.substringBeforeLast(sb.toString(),LabelConstant.SEPARATOR));
        }
        dto.setIsPartitionTable(hiveTblDTO.getBasic().getIsParTbl()?ColumnType.PARTITION_KEY.getCode():ColumnType.COLUMN.getCode());

        if (!CollectionUtils.isEmpty(hiveTblDTO.getColumns())){
            hiveTblDTO.getColumns().forEach(hiveColumn->{
                TableColumnQueryDTO columnQueryDTO = new TableColumnQueryDTO();
                columnQueryDTO.setComment(hiveColumn.getComment());
                columnQueryDTO.setName(hiveColumn.getName());
                columnQueryDTO.setType(hiveColumn.getType());
                columnQueryDTO.setIsPartitionField(ColumnType.COLUMN.getCode());
                dto.columns.add(columnQueryDTO);
            });
        }

        if (!CollectionUtils.isEmpty(hiveTblDTO.getPartitionKeys())){
            hiveTblDTO.getPartitionKeys().forEach(hiveColumn->{
                TableColumnQueryDTO columnQueryDTO = new TableColumnQueryDTO();
                columnQueryDTO.setComment(hiveColumn.getComment());
                columnQueryDTO.setName(hiveColumn.getName());
                columnQueryDTO.setType(hiveColumn.getType());
                columnQueryDTO.setIsPartitionField(ColumnType.PARTITION_KEY.getCode());
                dto.columns.add(columnQueryDTO);
            });
        }
        TableHeadlineDTO headlineDTO = new TableHeadlineDTO();
        headlineDTO.setStorageType(0);
        headlineDTO.setTableType(0);
        headlineDTO.setEntityType(1);
        dto.setHeadline(headlineDTO);

        dto.setStats(TableStatsDTO.from(hiveTblStatsDTO,0));
        return dto;
    }
}
