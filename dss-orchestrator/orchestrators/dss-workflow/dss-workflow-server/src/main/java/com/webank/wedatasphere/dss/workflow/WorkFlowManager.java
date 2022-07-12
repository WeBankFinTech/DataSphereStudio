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

package com.webank.wedatasphere.dss.workflow;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.common.label.DSSLabel;
import com.webank.wedatasphere.dss.orchestrator.common.protocol.RequestConvertOrchestrations;
import com.webank.wedatasphere.dss.orchestrator.common.protocol.ResponseOperateOrchestrator;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.workflow.common.entity.DSSFlow;
import com.webank.wedatasphere.dss.workflow.common.protocol.RequestSubFlowContextIds;
import com.webank.wedatasphere.dss.workflow.common.protocol.ResponseSubFlowContextIds;
import com.webank.wedatasphere.dss.workflow.entity.DSSFlowImportParam;
import org.apache.linkis.common.exception.ErrorException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface WorkFlowManager {

    /**
     * create a new workflow
     *
     * @param userName
     * @param workflowName
     * @param contextIDStr
     * @param description
     * @param parentFlowID
     * @param uses
     * @return
     */
    DSSFlow createWorkflow(String userName,
                           Long projectId,
                           String workflowName,
                           String contextIDStr,
                           String description,
                           Long parentFlowID,
                           String uses,
                           List<String> linkedAppConnNames,
                           List<DSSLabel> dssLabels,
                           String orcVersion,
                           String schedulerAppConn) throws DSSErrorException, JsonProcessingException;


    /**
     * 根据已有的flow内容，拷贝一份新的flow,区分BML文件，用于绑定不同的版本
     *
     * @param userName
     * @param rootFlowId
     * @param contextIdStr
     * @param description
     * @return
     * @throws DSSErrorException
     */
    DSSFlow copyRootFlowWithSubFlows(String userName,
                                     Long rootFlowId,
                                     Workspace workspace,
                                     String projectName,
                                     String contextIdStr,
                                     String orcVersion,
                                     String description,
                                     List<DSSLabel> dssLabels) throws DSSErrorException, IOException;

    DSSFlow queryWorkflow(String userName, Long rootFlowId) throws DSSErrorException;


    void updateWorkflow(String userName,
                        Long flowID,
                        String flowName,
                        String description,
                        String uses) throws DSSErrorException;


    void deleteWorkflow(String userName,
                        Long flowID) throws DSSErrorException;


    Map<String, Object> exportWorkflow(String userName,
                                       Long flowID,
                                       Long dssProjectId,
                                       String projectName,
                                       Workspace workspace,
                                       List<DSSLabel> dssLabels) throws Exception;

    List<DSSFlow> importWorkflow(String userName,
                                 String resourceId,
                                 String bmlVersion,
                                 DSSFlowImportParam dssFlowImportParam,
                                 List<DSSLabel> dssLabels) throws Exception;

    ResponseOperateOrchestrator convertWorkflow(RequestConvertOrchestrations requestConversionWorkflow) throws DSSErrorException;

    /**
     * 获取子工作流contextId
     *
     * @param requestSubFlowContextIds
     * @return
     */
    ResponseSubFlowContextIds getSubFlowContextIdsByFlowIds(RequestSubFlowContextIds requestSubFlowContextIds) throws ErrorException;

    void batchDeleteBmlResource(List<Long> flowIdList);


}
