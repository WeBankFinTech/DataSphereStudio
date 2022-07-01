package com.webank.wedatasphere.dss.data.api.server.entity.response;

import com.webank.wedatasphere.dss.data.api.server.entity.ApiAuth;
import lombok.Data;

@Data
public class ApiAuthInfo extends ApiAuth {
    private String groupName;
}
