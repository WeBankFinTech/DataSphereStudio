/*
 * Copyright 2019 WeBank
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.webank.wedatasphere.dss.framework.appconn.service.impl;

import com.webank.wedatasphere.dss.appconn.manager.entity.AppConnInfo;
import com.webank.wedatasphere.dss.appconn.manager.entity.AppInstanceInfo;
import com.webank.wedatasphere.dss.appconn.manager.service.AppConnInfoService;
import com.webank.wedatasphere.dss.framework.appconn.conf.AppConnConf;
import com.webank.wedatasphere.dss.framework.appconn.dao.AppConnMapper;
import com.webank.wedatasphere.dss.framework.appconn.dao.AppInstanceMapper;
import com.webank.wedatasphere.dss.framework.appconn.entity.AppConnBean;
import com.webank.wedatasphere.dss.framework.appconn.utils.AppConnServiceUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AppConnInfoServiceImpl implements AppConnInfoService {
    @Autowired
    private AppConnMapper appConnMapper;
    @Autowired
    private AppInstanceMapper appInstanceMapper;

    @Override
    public List<? extends AppConnInfo> getAppConnInfos() {
        List<AppConnBean> appConnBeans = appConnMapper.getAllAppConnBeans().stream()
                .filter(appConnBean -> !AppConnConf.DISABLED_APP_CONNS.contains(appConnBean.getAppConnName()))
                .collect(Collectors.toList());
        appConnBeans.forEach(appConnBean -> {
            String resource = appConnBean.getResource();
            if(StringUtils.isNotBlank(resource)) {
                appConnBean.setAppConnResource(AppConnServiceUtils.stringToResource(resource).getResource());
            }
        });
        return appConnBeans;
    }

    @Override
    public AppConnInfo getAppConnInfo(String appConnName) {
        AppConnBean appConnBean = appConnMapper.getAppConnBeanByName(appConnName);
        if (StringUtils.isNotBlank(appConnBean.getResource())) {
            appConnBean.setAppConnResource(AppConnServiceUtils.stringToResource(appConnBean.getResource()).getResource());
        }
        return appConnBean;
    }

    @Override
    public List<? extends AppInstanceInfo> getAppInstancesByAppConnInfo(AppConnInfo appConnInfo) {
        Long id = ((AppConnBean) appConnInfo).getId();
        return appInstanceMapper.getAppInstancesByAppConnId(id);
    }

    @Override
    public List<? extends AppInstanceInfo> getAppInstancesByAppConnName(String appConnName) {
        AppConnBean appConnBean = appConnMapper.getAppConnBeanByName(appConnName);
        return getAppInstancesByAppConnInfo(appConnBean);
    }
}
