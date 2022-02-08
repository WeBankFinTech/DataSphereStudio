package com.webank.wedatasphere.dss.data.api.server.entity.response;

import java.util.List;


import lombok.Data;

@Data
public class ApiGroupInfo {
    private int groupId;
    private String groupName;
    private List<ApiListInfo> apis;
}
