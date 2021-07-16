package com.webank.wedatasphere.dss.framework.dbapi.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.webank.wedatasphere.dss.framework.dbapi.dao.DSSApiConfigMapper;
import com.webank.wedatasphere.dss.framework.dbapi.entity.ApiConfig;
import com.webank.wedatasphere.dss.framework.dbapi.entity.ApiGroup;
import com.webank.wedatasphere.dss.framework.dbapi.entity.response.ApiGroupInfo;
import com.webank.wedatasphere.dss.framework.dbapi.entity.response.ApiListInfo;
import com.webank.wedatasphere.dss.framework.dbapi.service.DSSApiConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DSSApiConfigServiceImpl extends ServiceImpl<DSSApiConfigMapper, ApiConfig> implements DSSApiConfigService {
    @Autowired
    DSSApiConfigMapper dssApiConfigMapper;

    public void saveApi(ApiConfig apiConfig){

        String apiType = apiConfig.getApiType();
        if("GUIDE".equals(apiType)){

        }

        this.save(apiConfig);


    }

    @Override
    public void addGroup(ApiGroup apiGroup) {
      dssApiConfigMapper.addApiGroup(apiGroup);
    }

    @Override
    public List<ApiGroupInfo> getGroupList(String workspaceId) {
        List<ApiGroupInfo>  apiGroupInfoList = dssApiConfigMapper.getGroupByWorkspaceId(workspaceId);
        for(ApiGroupInfo apiGroupInfo : apiGroupInfoList){
            int groupId = apiGroupInfo.getGroupId();
           apiGroupInfo.setApis(dssApiConfigMapper.getApiListByGroup(groupId));
        }
        return apiGroupInfoList;
    }




}
