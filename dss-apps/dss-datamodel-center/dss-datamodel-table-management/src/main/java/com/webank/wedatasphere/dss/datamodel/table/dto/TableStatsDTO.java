package com.webank.wedatasphere.dss.datamodel.table.dto;

import com.webank.wedatasphere.dss.data.governance.entity.HiveTblStatsDTO;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@ToString
public class TableStatsDTO {

    private Long id;

    private String dataBase;

    private String name;

    private Date createTime;

    private Date updateTime;

    /**
     * 字段数
     */
    private Integer columnCount = 0;

    /**
     * 存储大小
     */
    private Long storageSize = 0L;

    /**
     * 文件数
     */
    private Integer fileCount = 0;

    /**
     * 分区数
     */
    private Integer partitionCount = 0;

    /**
     * 访问次数
     */
    private Integer accessCount = 0;

    /**
     * 收藏次数
     */
    private Integer collectCount = 0;

    /**
     * 引用次数
     */
    private Integer refCount = 0;

    /**
     * 版本信息：默认1
     */
    private String version;


    public static TableStatsDTO from(HiveTblStatsDTO hiveTblStatsDTO,Integer collectCount){
        TableStatsDTO dto = new TableStatsDTO();
        dto.setColumnCount(hiveTblStatsDTO.getColumnCount());
        dto.setFileCount(hiveTblStatsDTO.getNumFiles());
        dto.setPartitionCount(hiveTblStatsDTO.getPartitionCount());
        dto.setStorageSize(hiveTblStatsDTO.getTotalSize());
        dto.setCollectCount(collectCount);
        return dto;
    }
}
