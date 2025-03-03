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

package com.webank.wedatasphere.dss.workflow.io.input;


import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.common.label.DSSLabel;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.workflow.common.entity.DSSFlow;
import com.webank.wedatasphere.dss.workflow.common.entity.DSSFlowRelation;

import java.io.IOException;
import java.util.List;

public interface WorkFlowInputService {

    void inputWorkFlowNew(String userName, DSSFlow dssFlow, String projectName,
                          String flowCodePath, String flowMetaPath, Long parentFlowId, Workspace workspace,
                          String orcVersion, String contextId, List<DSSLabel> dssLabels) throws DSSErrorException,IOException;

    /**
     *
     * @param userName
     * @param dssFlow
     * @param projectName
     * @param inputProjectPath
     * @param parentFlowId
     * @param workspace
     * @param orcVersion
     * @throws DSSErrorException

     * @throws IOException
     */
    void inputWorkFlow(String userName, DSSFlow dssFlow, String projectName,
                       String inputProjectPath, Long parentFlowId, Workspace workspace,
                       String orcVersion, String contextId, List<DSSLabel> dssLabels) throws DSSErrorException,IOException;

    /**
     * save flow to db
     * @param projectId
     * @param userName
     * @param dssFlows
     * @param DSSFlowRelations
     * @return
     */
    List<DSSFlow> persistenceFlow(Long projectId, String userName, List<DSSFlow> dssFlows, List<DSSFlowRelation> DSSFlowRelations);
}
