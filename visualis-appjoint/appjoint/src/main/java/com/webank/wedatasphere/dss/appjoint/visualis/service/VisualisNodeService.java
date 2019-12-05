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

package com.webank.wedatasphere.dss.appjoint.visualis.service;


import com.webank.wedatasphere.dss.appjoint.exception.AppJointErrorException;
import com.webank.wedatasphere.dss.appjoint.execution.core.AppJointNode;
import com.webank.wedatasphere.dss.appjoint.service.AppJointUrlImpl;
import com.webank.wedatasphere.dss.appjoint.service.NodeService;
import com.webank.wedatasphere.dss.appjoint.service.session.Session;
import com.webank.wedatasphere.dss.appjoint.visualis.service.nodeservice.DashboardNodeService;
import com.webank.wedatasphere.dss.appjoint.visualis.service.nodeservice.DisplayNodeService;

import java.util.Map;

/**
 * Created by enjoyyin on 2019/11/5.
 */
public class VisualisNodeService extends AppJointUrlImpl implements NodeService {

    @Override
    public Map<String, Object> createNode(Session session, AppJointNode node,
                                          Map<String, Object> requestBody) throws AppJointErrorException {
        if (DisplayNodeService.getNodeType().equals(node.getNodeType())) {
            return DisplayNodeService.createNode(session, getBaseUrl(), String.valueOf(node.getProjectId()), node.getNodeType(), requestBody);
        } else if (DashboardNodeService.getNodeType().equals(node.getNodeType())) {
            return DashboardNodeService.createNode(session, getBaseUrl(), String.valueOf(node.getProjectId()), node.getNodeType(), requestBody);
        } else {
            throw new AppJointErrorException(42002, "cannot recognize the nodeType " + node.getNodeType());
        }
    }

    @Override
    public void deleteNode(Session session, AppJointNode node) throws AppJointErrorException {
        if (DisplayNodeService.getNodeType().equals(node.getNodeType())) {
            DisplayNodeService.deleteNode(session, getBaseUrl(), String.valueOf(node.getProjectId()), node.getNodeType(), node.getJobContent());
        } else if (DashboardNodeService.getNodeType().equals(node.getNodeType())) {
            DashboardNodeService.deleteNode(session, getBaseUrl(), String.valueOf(node.getProjectId()), node.getNodeType(), node.getJobContent());
        } else {
            throw new AppJointErrorException(42002, "cannot recognize the nodeType " + node.getNodeType());
        }
    }

    @Override
    public Map<String, Object> updateNode(Session session, AppJointNode node,
                                          Map<String, Object> requestBody) throws AppJointErrorException {
        if (DisplayNodeService.getNodeType().equals(node.getNodeType())) {
            return DisplayNodeService.updateNode(session, getBaseUrl(), node.getProjectId(), node.getNodeType(), requestBody);
        } else if (DashboardNodeService.getNodeType().equals(node.getNodeType())) {
            return DashboardNodeService.updateNode(session, getBaseUrl(), node.getProjectId(), node.getNodeType(), requestBody);
        } else {
            throw new AppJointErrorException(42002, "cannot recognize the nodeType " + node.getNodeType());
        }
    }
}