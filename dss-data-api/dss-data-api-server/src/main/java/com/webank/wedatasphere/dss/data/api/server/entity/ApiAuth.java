package com.webank.wedatasphere.dss.data.api.server.entity;

import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/** @Classname DSSDataApiAuth @Description TODO @Date 2021/7/13 14:03 @Created by suyc */
@TableName(value = "dss_dataapi_auth")
@Data
public class ApiAuth implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long workspaceId;
    private String caller;
    private String token;
    //    @JsonSerialize(using= DateJsonSerializer.class)
    //    @JsonDeserialize(using= DateJsonDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date expire;

    private Long groupId;

    //    @JsonSerialize(using= DateJsonSerializer.class)
    //    @JsonDeserialize(using= DateJsonDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    private String createBy;
    //    @JsonSerialize(using= DateJsonSerializer.class)
    //    @JsonDeserialize(using= DateJsonDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    private String updateBy;
    private Integer isDelete;
}
