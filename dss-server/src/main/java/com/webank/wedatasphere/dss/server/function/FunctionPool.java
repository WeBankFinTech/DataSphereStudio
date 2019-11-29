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
package com.webank.wedatasphere.dss.server.function;

import com.webank.wedatasphere.dss.appjoint.execution.core.AppJointNode;
import com.webank.wedatasphere.dss.appjoint.service.NodeService;
import com.webank.wedatasphere.dss.appjoint.service.session.Session;

import java.util.Map;


public class FunctionPool {

    public static NodeServiceFunction deleteNode = (NodeService nodeService, Session session, AppJointNode node, Map<String, Object> requestBody)->{
        nodeService.deleteNode(session,node);
        return null;
    };

    public static NodeServiceFunction createNode = NodeService::createNode;

    public static NodeServiceFunction updateNode = NodeService::updateNode;

    // TODO: 2019/11/26 把projectService也纳入
}
