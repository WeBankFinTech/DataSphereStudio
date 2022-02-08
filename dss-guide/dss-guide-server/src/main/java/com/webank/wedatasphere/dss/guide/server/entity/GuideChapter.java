package com.webank.wedatasphere.dss.guide.server.entity;

import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/** @author suyc @Classname GuideChapter @Description TODO @Date 2022/1/13 20:10 @Created by suyc */
@Data
@TableName(value = "dss_guide_chapter")
public class GuideChapter {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long catalogId;

    private String title;
    private String titleAlias;
    private String content;
    private String contentHtml;

    private String createBy;
    private String updateBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
    // private Integer isDelete;
}
