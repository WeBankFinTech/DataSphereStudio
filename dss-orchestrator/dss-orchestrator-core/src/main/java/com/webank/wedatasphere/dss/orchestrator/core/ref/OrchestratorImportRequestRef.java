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

import com.webank.wedatasphere.dss.common.entity.IOEnv;
import com.webank.wedatasphere.dss.standard.app.development.publish.ImportRequestRef;
import com.webank.wedatasphere.dss.common.entity.DSSLabel;

import java.util.List;

/**
 * @author allenlliu
 * @date 2020/12/3 20:20
 */
public interface OrchestratorImportRequestRef extends ImportRequestRef {

    void setUserName(String userName);

    String getUserName();

    void setResourceId(String resourceId);

    String getResourceId();

    void setBmlVersion(String bmlVersion);

    String getBmlVersion();

    void setProjectId(Long projectId);

    Long getProjectId();

    void setProjectName(String projectName);

    String getProjectName();

    void setSourceEnv(IOEnv sourceEnv);

    IOEnv getSourceEnv();

    void setOrcVersion(String orcVersion);

    String getOrcVersion();

    void setWorkspaceName(String workspaceName);

    String getWorkspaceName();

    default String getOrchestratorName(){
        return "default_orc_name";
    }

    default void setOrchestratorName(String orchestratorName){

    }


    @Override
    List<DSSLabel> getDSSLabels();

    void setDSSLabels(List<DSSLabel> dssLabels);



}
