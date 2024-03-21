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

package com.webank.wedatasphere.dss.workflow.common.parser;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.webank.wedatasphere.dss.common.entity.Resource;
import com.webank.wedatasphere.dss.common.entity.node.DSSEdge;
import com.webank.wedatasphere.dss.common.entity.node.DSSNode;

import java.io.IOException;
import java.util.List;
import java.util.Map;


public interface WorkFlowParser {
    List<Resource> getWorkFlowResources(String workFlowJson);

    List<String> getParamConfTemplate(String workFlowJson);

    List<DSSNode> getWorkFlowNodes(String workFlowJson);

    List<DSSEdge> getWorkFlowEdges(String workFlowJson);

    List<String> getWorkFlowNodesJson(String workFlowJson);

    String updateFlowJsonWithKey(String workFlowJson, String key, Object value) throws IOException;

    String updateFlowJsonWithMap(String workFlowJson, Map<String, Object> props) throws JsonProcessingException;

    /**
     * 获取工作流中的某个字段
     * @param workFlowJson 工作流大json
     * @param key 字段名
     * @return 字段值
     */
    String getValueWithKey(String workFlowJson, String key) throws IOException;

}
