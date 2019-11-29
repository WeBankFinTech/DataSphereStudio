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

package com.webank.wedatasphere.dss.appjoint.service;

import com.webank.wedatasphere.dss.appjoint.execution.core.AppJointNode;
import com.webank.wedatasphere.dss.appjoint.exception.AppJointErrorException;
import com.webank.wedatasphere.dss.appjoint.service.session.Session;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by enjoyyin on 2019/10/10.
 */
public interface NodeService extends AppJointUrl {

    /**
     * 根据参数创建外部节点
     * @param session   连接会话
     * @param node 节点信息
     * @param requestBody 创建节点需要的参数，前端带过来的Json结构字符串。
     * @return 返回jobContent的Map，工作流会将该Map存储起来，作为该节点的关键关联信息，用于后续的CRUD和执行。
     */
    Map<String, Object> createNode(Session session, AppJointNode node,
                          Map<String, Object> requestBody) throws AppJointErrorException;

    void deleteNode(Session session, AppJointNode node) throws AppJointErrorException;

    Map<String, Object> updateNode(Session session, AppJointNode node, Map<String, Object> requestBody) throws AppJointErrorException;

    default Map<String, Object> refresh(Session session, AppJointNode node) throws AppJointErrorException {
        return null;
    }

    default void copyNode(Session session, AppJointNode newNode, AppJointNode oldNode) throws AppJointErrorException { }

    default void setNodeReadOnly(Session session, AppJointNode node) throws AppJointErrorException {}

    default List<AppJointNode> listNodes(Session session, AppJointNode node) throws AppJointErrorException {
        return new ArrayList<>();
    }

}
