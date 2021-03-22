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

package com.webank.wedatasphere.dss.standard.app.development.crud;

import java.util.Map;

public interface UpdateCSRequestRef extends UpdateRequestRef {

    void setNodeType(String nodeType);
    void setUserName(String userName);
    void setName(String name);
    void setJobContent(Map<String, Object> jobContent);
    void setProjectId(long projectId);
    void setProjectName(String projectName);
    void setOrcName(String flowName);
    void setOrcId(long flowId);
    void setContextID(String contextID);
}
