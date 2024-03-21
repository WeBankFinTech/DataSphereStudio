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

package com.webank.wedatasphere.dss.workflow.service;

import com.webank.wedatasphere.dss.common.entity.BmlResource;
import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.standard.app.development.ref.ExportResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.dss.workflow.entity.AbstractAppConnNode;
import com.webank.wedatasphere.dss.workflow.entity.CommonAppConnNode;
import com.webank.wedatasphere.dss.workflow.entity.NodeGroup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public interface WorkflowNodeService {

    List<NodeGroup> listNodeGroups();

    /**
     * 根据参数创建外部节点
     * @param node 节点信息
     * @return 返回jobContent的Map，工作流会将该Map存储起来，作为该节点的关键关联信息，用于后续的CRUD和执行。
     */
    Map<String, Object> createNode(String userName, CommonAppConnNode node) throws ExternalOperationFailedException;

    void deleteNode(String userName, CommonAppConnNode node) throws ExternalOperationFailedException;

    Map<String, Object> updateNode(String userName, CommonAppConnNode node) throws ExternalOperationFailedException;

    default Map<String, Object> refresh(String userName, CommonAppConnNode node) {
        return null;
    }

    Map<String, Object> copyNode(String userName, CommonAppConnNode newNode, CommonAppConnNode oldNode, String orcVersion) throws IOException, DSSErrorException;

    default void setNodeReadOnly(String userName, CommonAppConnNode node) {
    }

    default List<AbstractAppConnNode> listNodes(String userName, CommonAppConnNode node) {
        return new ArrayList<>();
    }

    Map<String, Object> importNode(String userName,
                                   CommonAppConnNode node,
                                   Supplier<Map<String, Object>> getBmlResourceMap,
                                   Supplier<Map<String, Object>> getStreamResourceMap,
                                   String orcVersion) throws Exception;

    ExportResponseRef exportNode(String userName, CommonAppConnNode node);

    String getNodeJumpUrl(Map<String, Object> params, CommonAppConnNode node, String userName) throws ExternalOperationFailedException;
    byte[] getNodeIcon(String nodeType);

}
