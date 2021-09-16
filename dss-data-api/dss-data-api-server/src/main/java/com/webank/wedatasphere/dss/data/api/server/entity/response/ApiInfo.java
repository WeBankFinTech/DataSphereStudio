package com.webank.wedatasphere.dss.data.api.server.entity.response;

import lombok.Data;

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

    private String groupName;
    private String datasourceName;

}
