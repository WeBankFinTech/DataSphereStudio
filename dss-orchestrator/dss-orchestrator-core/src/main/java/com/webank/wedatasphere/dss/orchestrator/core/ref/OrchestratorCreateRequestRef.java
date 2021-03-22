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

package com.webank.wedatasphere.dss.orchestrator.core.ref;

import com.webank.wedatasphere.dss.orchestrator.common.entity.DSSOrchestratorInfo;
import com.webank.wedatasphere.dss.standard.app.development.crud.CreateRequestRef;
import com.webank.wedatasphere.dss.common.entity.DSSLabel;

import java.util.List;


/**
 * @author allenlliu
 * @date 2020/11/26 17:21
 */
public interface OrchestratorCreateRequestRef extends CreateRequestRef {

    void setUserName(String userName);

    String getUserName();


    void setWorkspaceName(String workspaceName);

    String getWorkspaceName();


    void setProjectName(String projectName);

    String getProjectName();

    void setProjectId(Long projectId);

    Long getProjectId();

    void setDssOrchestratorInfo(DSSOrchestratorInfo dssOrchestratorInfo);

    DSSOrchestratorInfo getDSSOrchestratorInfo();

    void setContextIDStr(String contextIDStr);

    String getContextIDStr();

    void setDSSLabels(List<DSSLabel> dssLabels);

    @Override
    List<DSSLabel> getDSSLabels();
}

