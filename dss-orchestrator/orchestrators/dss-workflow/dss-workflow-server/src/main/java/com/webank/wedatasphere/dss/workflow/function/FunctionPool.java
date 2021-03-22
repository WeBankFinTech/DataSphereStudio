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
package com.webank.wedatasphere.dss.workflow.function;

import com.webank.wedatasphere.dss.workflow.entity.AbstractAppConnNode;
import com.webank.wedatasphere.dss.workflow.service.WorkflowNodeService;

import java.util.Map;

/**
 * Created by v_wbjftang on 2019/11/26.
 */
public class FunctionPool {

    public static NodeServiceFunction deleteNode = (String userName, WorkflowNodeService nodeService, AbstractAppConnNode node, Map<String, Object> requestBody)->{
        node.setJobContent(requestBody);
        nodeService.deleteNode(userName,node);
        return null;
    };

    public static NodeServiceFunction createNode = (String userName, WorkflowNodeService nodeService, AbstractAppConnNode node, Map<String, Object> requestBody)->

    {
        node.setJobContent(requestBody);
        return nodeService.createNode(userName, node);
    };



    public static NodeServiceFunction updateNode = (String userName, WorkflowNodeService nodeService, AbstractAppConnNode node, Map<String, Object> requestBody)->

    {
        node.setJobContent(requestBody);
        return nodeService.updateNode(userName, node);
    };

}
