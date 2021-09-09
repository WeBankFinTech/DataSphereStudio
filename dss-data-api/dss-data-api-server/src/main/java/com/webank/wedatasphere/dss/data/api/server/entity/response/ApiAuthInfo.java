package com.webank.wedatasphere.dss.data.api.server.entity.response;

import com.webank.wedatasphere.dss.data.api.server.entity.ApiAuth;
import lombok.Data;

/**
 * @Classname ApiAuthInfo
 * @Description TODO
 * @Date 2021/9/9 9:25
 * @Created by suyc
 */
@Data
public class ApiAuthInfo extends ApiAuth {
    private String groupName;
}
