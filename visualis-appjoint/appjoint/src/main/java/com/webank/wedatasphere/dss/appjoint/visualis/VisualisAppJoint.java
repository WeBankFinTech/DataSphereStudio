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

package com.webank.wedatasphere.dss.appjoint.visualis;

import com.webank.wedatasphere.dss.appjoint.AppJoint;
import com.webank.wedatasphere.dss.appjoint.execution.NodeExecution;
import com.webank.wedatasphere.dss.appjoint.service.AppJointUrlImpl;
import com.webank.wedatasphere.dss.appjoint.service.NodeService;
import com.webank.wedatasphere.dss.appjoint.service.SecurityService;
import com.webank.wedatasphere.dss.appjoint.visualis.service.VisualisNodeService;
import com.webank.wedatasphere.dss.appjoint.visualis.service.VisualisSecurityService;
import com.webank.wedatasphere.dss.appjoint.visualis.execution.VisualisNodeExecution;

import java.util.Map;

/**
 * Created by enjoyyin on 2019/11/6.
 */
public class VisualisAppJoint extends AppJointUrlImpl implements AppJoint {

    private SecurityService securityService;

    private NodeExecution nodeExecution;

    private NodeService nodeService;

    @Override
    public String getAppJointName() {
        return "visualis";
    }

    @Override
    public void init(String baseUrl, Map<String, Object> params) {
        securityService = new VisualisSecurityService();
        securityService.setBaseUrl(baseUrl);
        nodeExecution = new VisualisNodeExecution();
        nodeExecution.setBaseUrl(baseUrl);
        nodeService = new VisualisNodeService();
        nodeService.setBaseUrl(baseUrl);
    }

    @Override
    public SecurityService getSecurityService() {
        return securityService;
    }

    @Override
    public NodeService getNodeService() {
        return nodeService;
    }

    @Override
    public NodeExecution getNodeExecution() {
        return nodeExecution;
    }
}
