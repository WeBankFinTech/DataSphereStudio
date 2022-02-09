package com.webank.wedatasphere.dss.data.api.server.entity.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Classname ApiInfo
 * @Description TODO
 * @Date 2021/7/19 16:20
 * @Created by suyc
 */

@Data
public class ApiInfo {
    private Long id;
    private String apiName;
    private String apiPath;
    private String apiType;
    private Integer status;
    private String label;
    private String createBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    private String updateBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
    private String groupName;
    private String datasourceName;
    private int isTest;

}
