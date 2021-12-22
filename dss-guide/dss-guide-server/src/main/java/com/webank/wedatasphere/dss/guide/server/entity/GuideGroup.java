package com.webank.wedatasphere.dss.guide.server.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author suyc
 * @Classname GuideGroup
 * @Description TODO
 * @Date 2021/12/21 10:54
 * @Created by suyc
 */
@Data
@TableName(value = "dss_guide_group")
public class GuideGroup {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String path;
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
    private Integer isDelete;

}
