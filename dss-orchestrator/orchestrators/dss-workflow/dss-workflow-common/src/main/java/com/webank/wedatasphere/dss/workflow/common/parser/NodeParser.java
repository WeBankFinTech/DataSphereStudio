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


import com.webank.wedatasphere.dss.common.entity.Resource;

import java.io.IOException;
import java.util.List;
import java.util.Map;


public interface NodeParser {
    String updateNodeResource(String nodeJson, List<Resource> resources) throws IOException;
    String updateNodeJobContent(String nodeJson, Map<String, Object> content) throws IOException;
    String updateSubFlowID(String nodeJson, long subflowId) throws IOException;
    String getNodeValue(String key, String nodeJson) throws IOException;
    List<Resource> getNodeResource(String nodeJson);

    Map<String, Object> getNodeJobContent(String nodeJson) throws IOException;
}
