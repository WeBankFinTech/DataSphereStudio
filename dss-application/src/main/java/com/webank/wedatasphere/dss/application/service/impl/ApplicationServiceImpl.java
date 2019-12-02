/*
 * Copyright 2019 WeBank
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
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

package com.webank.wedatasphere.dss.application.service.impl;

import com.webank.wedatasphere.dss.appjoint.AppJoint;
import com.webank.wedatasphere.dss.appjoint.exception.AppJointErrorException;
import com.webank.wedatasphere.dss.appjoint.loader.AppJointLoader;
import com.webank.wedatasphere.dss.application.dao.ApplicationMapper;
import com.webank.wedatasphere.dss.application.entity.Application;
import com.webank.wedatasphere.dss.application.service.ApplicationService;
import com.webank.wedatasphere.dss.common.exception.DSSRuntimeException;
import com.webank.wedatasphere.dss.common.utils.DSSExceptionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.htrace.fasterxml.jackson.databind.JavaType;
import org.apache.htrace.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by chaogefeng on 2019/10/10.
 */
@Service
public class ApplicationServiceImpl implements ApplicationService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ApplicationMapper applicationMapper;

    @Override
    public List<String> listApplicationNames() {
        return applicationMapper.listApplicationNames();
    }

    @Override
    public List<Application> listApplications() {
        return applicationMapper.listApplications();
    }

    @Override
    public Application getApplication(String appName) {
        return applicationMapper.getApplication(appName);
    }

    @Override
    public AppJoint getAppjoint(String nodeType) throws AppJointErrorException {
        Application application = getApplicationbyNodeType(nodeType);
        AppJoint appJoint = null;
        try {
            appJoint = loadAppjoint(application);
        } catch (Exception e) {
            logger.error("load appJoint failed:",e);
            throw new AppJointErrorException(90000,"load appJoint failed:"+e.getMessage());
        }
        return appJoint;
    }

    @Override
    public List<AppJoint> listAppjoint()throws AppJointErrorException{
        List<Application> applications = listApplications();
        List<AppJoint> appJoints = null;
        try {
            appJoints = applications.stream().filter(Application::getExistsProjectService).map(DSSExceptionUtils.map(this::loadAppjoint)).collect(Collectors.toList());
        }catch (DSSRuntimeException e){
            logger.error("load appJoint failed:",e);
            throw new AppJointErrorException(90000,"load appJoint failed:"+e.getMessage());
        }
        return appJoints;
    }

    @Override
    public Application getApplicationbyNodeType(String nodeType) {
        return applicationMapper.getApplicationbyNodeType(nodeType);
    }

    private AppJoint loadAppjoint(Application application) throws Exception {
        String enhanceJson = application.getEnhanceJson();
        if(StringUtils.isBlank(enhanceJson)) enhanceJson = "{}";
        ObjectMapper om = new ObjectMapper();
        JavaType javaType = om.getTypeFactory().constructParametricType(Map.class,String.class,Object.class);
        Map<String,Object> parmas = om.readValue(enhanceJson, javaType);
        return AppJointLoader.getAppJointLoader().getAppJoint(application.getUrl(), application.getName(), parmas);
    }
}
