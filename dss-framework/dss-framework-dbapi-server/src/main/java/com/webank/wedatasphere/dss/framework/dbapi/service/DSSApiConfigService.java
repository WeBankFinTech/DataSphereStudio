package com.webank.wedatasphere.dss.framework.dbapi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.webank.wedatasphere.dss.framework.dbapi.entity.ApiConfig;
import com.webank.wedatasphere.dss.framework.dbapi.entity.ApiGroup;
import com.webank.wedatasphere.dss.framework.dbapi.entity.response.ApiGroupInfo;
import com.webank.wedatasphere.dss.framework.dbapi.entity.response.ApiListInfo;

import java.util.List;

public interface DSSApiConfigService extends IService<ApiConfig> {

    void addGroup(ApiGroup apiGroup);
    List<ApiGroupInfo> getGroupList(String workspaceId);
}
