package com.webank.wedatasphere.dss.framework.dbapi.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.webank.wedatasphere.dss.framework.dbapi.dao.ApiConfigMapper;
import com.webank.wedatasphere.dss.framework.dbapi.entity.ApiConfig;
import com.webank.wedatasphere.dss.framework.dbapi.service.ApiConfigService;
import org.springframework.stereotype.Service;

@Service
public class ApiConfigServiceImpl extends ServiceImpl<ApiConfigMapper, ApiConfig> implements ApiConfigService {

    public void saveApi(ApiConfig apiConfig){

        String apiType = apiConfig.getApiType();
        if("GUIDE".equals(apiType)){

        }

        this.save(apiConfig);


    }

}
