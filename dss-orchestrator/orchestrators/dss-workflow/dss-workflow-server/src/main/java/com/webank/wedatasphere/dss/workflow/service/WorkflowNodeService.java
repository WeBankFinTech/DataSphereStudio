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

package com.webank.wedatasphere.dss.workflow.service;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.dss.workflow.entity.AbstractAppConnNode;
import com.webank.wedatasphere.dss.workflow.entity.NodeGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by v_wbjftang on 2019/10/10.
 */
public interface WorkflowNodeService {

    List<NodeGroup> listNodeGroups();

    /**
     * 根据参数创建外部节点
     * @param node 节点信息
     * @return 返回jobContent的Map，工作流会将该Map存储起来，作为该节点的关键关联信息，用于后续的CRUD和执行。
     */
    Map<String, Object> createNode(String userName, AbstractAppConnNode node
                                   ) throws ExternalOperationFailedException, InstantiationException, IllegalAccessException;

    void deleteNode(String userName, AbstractAppConnNode node) throws ExternalOperationFailedException, InstantiationException, IllegalAccessException;

    Map<String, Object> updateNode(String userName, AbstractAppConnNode node) throws ExternalOperationFailedException, InstantiationException, IllegalAccessException;

    default Map<String, Object> refresh(String userName,AbstractAppConnNode node) {
        return null;
    }

    default void copyNode(String userName, AbstractAppConnNode newNode, AbstractAppConnNode oldNode)  { }

    default void setNodeReadOnly(String userName, AbstractAppConnNode node)  {}

    default List<AbstractAppConnNode> listNodes(String userName, AbstractAppConnNode node)  {
        return new ArrayList<>();
    }

    default Map<String, Object> exportNode(String userName, AbstractAppConnNode node) {
        return null;
    }

    default Map<String, Object> importNode(String userName, AbstractAppConnNode node, Map<String, Object> resourceMap, Workspace workspace, String orcVersion) throws Exception {
        return null;
    }

    String getNodeJumpUrl(Map<String, Object> params, AbstractAppConnNode node) throws ExternalOperationFailedException;

}
