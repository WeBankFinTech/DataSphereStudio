package com.webank.wedatasphere.dss.guide.server.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * @author suyc
 * @Classname GuideCatalog
 * @Description TODO
 * @Date 2022/1/13 16:40
 * @Created by suyc
 */
@Data
@TableName(value = "dss_guide_catalog")
public class GuideCatalog {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private Long parentId;

    private String title;
    private String description;

    private String createBy;
    private String updateBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
    //private Integer isDelete;
}
