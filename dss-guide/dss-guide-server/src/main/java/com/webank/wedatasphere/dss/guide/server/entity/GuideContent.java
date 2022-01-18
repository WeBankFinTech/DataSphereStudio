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
 * @Classname GuideContent
 * @Description TODO
 * @Date 2021/12/21 13:48
 * @Created by suyc
 */
@Data
@TableName(value = "dss_guide_content")
public class GuideContent {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long groupId;
    private String path;

    private Integer type;
    private String title;
    private String titleAlias;
    private String seq;
    private String content;
    private String contentHtml;

    private String createBy;
    private String updateBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
    private Integer isDelete;
}
