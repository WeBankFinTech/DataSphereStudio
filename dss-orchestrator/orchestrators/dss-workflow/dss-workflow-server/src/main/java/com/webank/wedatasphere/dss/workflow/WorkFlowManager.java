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

package com.webank.wedatasphere.dss.workflow;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.webank.wedatasphere.dss.common.entity.DSSLabel;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.workflow.common.entity.DSSFlow;
import com.webank.wedatasphere.dss.workflow.entity.DSSFlowImportParam;
import com.webank.wedatasphere.dss.common.exception.DSSErrorException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author allenlliu
 * @date 2020/10/21 03:25 PM
 */

public interface WorkFlowManager {

    /**
     * create a new workflow
     * @param userName
     * @param workflowName
     * @param contextIDStr
     * @param description
     * @param parentFlowID
     * @param uses
     * @return
     */
   DSSFlow createWorkflow(String userName,
                          String workflowName,
                          String contextIDStr,
                          String description,
                          Long parentFlowID,
                          String uses,
                          List<String> linkedAppConnNames,
                          List<DSSLabel> dssLabels
                           ) throws DSSErrorException, JsonProcessingException;


    /**
     * 根据已有的flow内容，拷贝一份新的flow,区分BML文件，用于绑定不同的版本
     * @param userName
     * @param rootFlowId
     * @param contextIDStr
     * @param description
     * @return
     * @throws DSSErrorException
     */
    DSSFlow copyRootflowWithSubflows(String userName,
                                              long rootFlowId,
                                              String workspaceName,
                                              String projectName,
                                              String contextIDStr,
                                              String version,
                                              String description) throws DSSErrorException, IOException;

    DSSFlow queryWorkflow(String userName, Long rootFlowId) throws DSSErrorException;


  void  updateWorkflow(String userName,
                                 Long flowID,
                                 String flowName,
                                 String description,
                                 String uses) throws DSSErrorException;


  void  deleteWorkflow(String userName,
                                 Long flowID) throws DSSErrorException;


   Map<String,Object> exportWorkflow(String userName,
                                     Long flowID,
                                     Long dssProjectId,
                                     String projectName,
                                     Workspace workspace) throws Exception;

   List<DSSFlow>  importWorkflow(String userName,
                                  String resourceId,
                                  String bmlVersion,
                                  DSSFlowImportParam dssFlowImportParam) throws Exception;
}
