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

package com.webank.wedatasphere.dss.standard.app.development.execution;

import com.webank.wedatasphere.dss.standard.app.development.WorkspaceRequestRef;
import java.util.Map;

public interface ExecutionRequestRef extends WorkspaceRequestRef {

    /**
     * 获取的node所属的project的id
     * @return
     */
    long getProjectId();

    /**
     * 获取node所属的project的name
     * @return
     */
    String getProjectName();
    String getOrchestratorName();

    String getOrchestratorVersion();

    long getOrchestratorId();

    /**
     * 获取到Node的执行内容,执行内容是以Map的形式的
     * @return
     */
    Map<String, Object> getJobContent();

}
