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

import com.webank.wedatasphere.dss.common.entity.DSSLabel;
import com.webank.wedatasphere.dss.standard.app.development.publish.ExportRequestRef;


import java.util.List;

/**
 * @author allenlliu
 * @date 2020/12/3 17:50
 */
public interface OrchestratorExportRequestRef extends ExportRequestRef {

     void setUserName(String userName);

     String getUserName();

     void setAppId(Long appId);

     Long getAppId();

     void setProjectId(Long projectId);

     Long getProjectId();

     void setProjectName(String projectName);

     String getProjectName();

     void setOrchestratorId(Long orchestratorId);

     Long getOrchestratorId();

     String getWorkspaceName();

     void setWorkspaceName(String workspaceName);

     void setOrchestratorVersionId(Long orchestratorVersionId);

     Long getOrchestratorVersionId();


     @Override
     List<DSSLabel>  getDSSLabels();

     void setDSSLabels(List<DSSLabel> dssLabels);

     boolean getAddOrcVersionFlag();

     void setAddOrcVersionFlag(boolean addOrcVersion);



}
