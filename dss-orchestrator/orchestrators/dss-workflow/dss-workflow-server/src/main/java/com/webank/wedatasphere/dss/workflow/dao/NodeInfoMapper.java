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

package com.webank.wedatasphere.dss.workflow.dao;


import com.webank.wedatasphere.dss.workflow.dto.NodeTypeDO;
import com.webank.wedatasphere.dss.workflow.entity.NodeGroup;
import com.webank.wedatasphere.dss.workflow.entity.NodeInfo;
import com.webank.wedatasphere.dss.workflow.entity.NodeUIInfo;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;


public interface NodeInfoMapper {

    List<NodeGroup> listNodeGroups();

    NodeInfo getWorkflowNodeByType(String nodeType);

    List<NodeTypeDO> getWorkflowNodeNumberType();

    List<NodeUIInfo> queryNodeUIInfoList(@Param("nodeTypeList") List<String> nodeTypeList);

    List<NodeUIInfo> getNodeUIInfoByNodeType(@Param("nodeType") String nodeType);

    List<NodeInfo> getNodeTypeByGroupName(@Param("groupName") String groupName,@Param("nodeTypeName") String nodeTypeName);

    List<NodeInfo> getWorkflowNodeList();
}

