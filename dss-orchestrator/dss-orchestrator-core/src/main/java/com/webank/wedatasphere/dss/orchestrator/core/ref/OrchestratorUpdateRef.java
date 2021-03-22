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
import com.webank.wedatasphere.dss.orchestrator.common.entity.DSSOrchestratorInfo;
import com.webank.wedatasphere.dss.standard.common.entity.ref.RequestRef;

import java.util.List;

/**
 * @author allenlliu
 * @date 2020/11/27 15:48
 */
public interface OrchestratorUpdateRef  extends RequestRef {

    void setUserName(String userName);

    String getUserName();

    void setOrcID(Long orcID);

    Long getOrcID();

    void setDescription(String description);

    String getDescription();

    void setUses(String uses);

    String getUses();

    void setOrchestratorName(String name);
    String getOrchestratorName();

    String getWorkspaceName();

    void setWorkspaceName(String workspaceName);

    String getProjectName();

    void setProjectName(String projectName);

    DSSOrchestratorInfo getOrchestratorInfo();

    void setOrchestratorInfo(DSSOrchestratorInfo dssOrchestratorInfo);

    @Override
    List<DSSLabel> getDSSLabels();

    void setDSSLabels(List<DSSLabel> dssLabels);




}
