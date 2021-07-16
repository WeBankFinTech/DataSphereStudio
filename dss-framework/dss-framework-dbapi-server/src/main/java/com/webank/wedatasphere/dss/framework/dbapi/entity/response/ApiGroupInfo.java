package com.webank.wedatasphere.dss.framework.dbapi.entity.response;

import lombok.Data;

import java.util.List;
@Data
public class ApiGroupInfo {
    private int groupId;
    private String groupName;
    private List<ApiListInfo> apis;
}
