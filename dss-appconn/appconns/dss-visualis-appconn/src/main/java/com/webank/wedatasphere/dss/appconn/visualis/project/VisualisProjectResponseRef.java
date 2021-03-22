/*
 *
 *  * Copyright 2019 WeBank
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.webank.wedatasphere.dss.appconn.visualis.project;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.Maps;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectResponseRef;
import com.webank.wedatasphere.dss.standard.common.desc.AppInstance;
import com.webank.wedatasphere.dss.standard.common.entity.ref.AbstractResponseRef;
import com.webank.wedatasphere.linkis.server.BDPJettyServerHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class VisualisProjectResponseRef extends AbstractResponseRef implements ProjectResponseRef {

    private static final Logger LOGGER = LoggerFactory.getLogger(VisualisProjectResponseRef.class);
    private Long projectRefId;
    private AppInstance appInstance;
    private String errorMsg;

    protected VisualisProjectResponseRef(String responseBody, int status) throws Exception {
        super(responseBody, status);
        responseMap = BDPJettyServerHelper.jacksonJson().readValue(responseBody, Map.class);
    }

    @Override
    public Long getProjectRefId() {
        return projectRefId;
    }

    @Override
    public Map<AppInstance, Long> getProjectRefIds() {
        Map<AppInstance, Long> projectRefIdsMap = Maps.newHashMap();
        projectRefIdsMap.put(appInstance, projectRefId);
        return projectRefIdsMap;
    }

    @Override
    public Map<String, Object> toMap() {
        return responseMap;
    }

    @Override
    public String getErrorMsg() {
        return errorMsg;
    }

    public void setProjectRefId(Long projectRefId) {
        this.projectRefId = projectRefId;
    }

    public AppInstance getAppInstance() {
        return appInstance;
    }

    public void setAppInstance(AppInstance appInstance) {
        this.appInstance = appInstance;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
