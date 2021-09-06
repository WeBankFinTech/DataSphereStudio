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

package com.webank.wedatasphere.dss.standard.app.development.ref;

import com.webank.wedatasphere.dss.common.label.DSSLabel;

import java.util.List;


public interface CommonRequestRef extends WorkspaceRequestRef {

    void setUserName(String userName);

    String getUserName();

    void setProjectId(Long projectId);

    Long getProjectId();

    void setProjectName(String projectName);

    String getProjectName();

    void setOrcName(String orcName);

    String getOrcName();

    void setOrcId(Long orcId);

    Long getOrcId();

    void setName(String name);

    void setWorkspaceName(String workspaceName);

    String getWorkspaceName();


    String getContextID();


    void setContextID(String contextIDStr);

    void setDSSLabels(List<DSSLabel> dssLabels);

    @Override
    List<DSSLabel> getDSSLabels();
}
