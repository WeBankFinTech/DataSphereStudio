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
import com.webank.wedatasphere.dss.common.entity.BmlResource;
import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.common.label.DSSLabel;
import com.webank.wedatasphere.dss.orchestrator.common.protocol.RequestConvertOrchestrations;
import com.webank.wedatasphere.dss.orchestrator.common.protocol.ResponseOperateOrchestrator;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.workflow.common.entity.DSSFlow;
import com.webank.wedatasphere.dss.workflow.common.protocol.*;
import com.webank.wedatasphere.dss.workflow.entity.DSSFlowImportParam;
import org.apache.linkis.common.exception.ErrorException;

import java.io.IOException;
import java.util.List;

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
                                     List<DSSLabel> dssLabels,String nodeSuffix,
                                     String newFlowName, Long newProjectId) throws DSSErrorException, IOException;


    DSSFlow queryWorkflow(String userName, Long rootFlowId) throws DSSErrorException;


    void updateWorkflow(String userName,
                        Long flowID,
                        String flowName,
                        String description,
                        String uses) throws DSSErrorException;


    void deleteWorkflow(String userName,
                        Long flowID) throws DSSErrorException;

    ResponseUnlockWorkflow unlockWorkflow(String userName, Long flowId, Boolean confirmDelete, Workspace workspace) throws DSSErrorException;
    /**
     * 导出工作流
     * @param userName
     * @param flowID
     * @param dssProjectId
     * @param projectName
     * @param workspace
     * @param dssLabels
     * @param exportExternalNodeAppConnResource 是否导出第三方节点的物料
     * @return 导出的工作流，以Bml资源的形式返回
     * @throws Exception
     */
    BmlResource exportWorkflowNew(String userName,
                                  Long flowID,
                                  Long dssProjectId,
                                  String projectName,
                                  Workspace workspace,
                                  List<DSSLabel> dssLabels,
            boolean exportExternalNodeAppConnResource) throws Exception;

    /**
     * 读取当前最新BML存储的指定文件内容，若文件不存在，则返回空字符串
     * @param userName
     * @param flowId
     * @param dssProjectId
     * @param projectName
     * @param workspace
     * @param dssLabels
     * @param exportExternalNodeAppConnResource
     * @param filePath：文件路径
     * @return
     * @throws Exception
     */
    String readWorkflowNew(String userName, Long flowId, Long dssProjectId,
                           String projectName, Workspace workspace,
                           List<DSSLabel> dssLabels,boolean exportExternalNodeAppConnResource,String filePath) throws Exception;
    /**
     * 导出工作流
     * @param userName
     * @param flowID
     * @param dssProjectId
     * @param projectName
     * @param workspace
     * @param dssLabels
     * @return 导出的工作流，以Bml资源的形式返回
     * @throws Exception
     */
    BmlResource exportWorkflow(String userName,
                               Long flowID,
                               Long dssProjectId,
                               String projectName,
                               Workspace workspace,
                               List<DSSLabel> dssLabels) throws Exception;

    List<DSSFlow> importWorkflowNew(String userName,
                                    String resourceId,
                                    String bmlVersion,
                                    DSSFlowImportParam dssFlowImportParam,
                                    List<DSSLabel> dssLabels) throws Exception;
    List<DSSFlow> importWorkflow(String userName,
                                 String resourceId,
                                 String bmlVersion,
                                 DSSFlowImportParam dssFlowImportParam,
                                 List<DSSLabel> dssLabels) throws Exception;

    /**
     * 将工作流转化为调度系统的工作流，即发布工作流到调度系统，成为一次调度任务。
     * 1.从db、物料包中拿到工作流的元数据信息（flowjson)
     * 2.将工作流打包，发到调度系统
     * @param requestConversionWorkflow
     * @return
     * @throws DSSErrorException
     */
    ResponseOperateOrchestrator convertWorkflow(RequestConvertOrchestrations requestConversionWorkflow) throws DSSErrorException;

    /**
     * 获取子工作流contextId
     *
     * @param requestSubFlowContextIds
     * @return
     */
    ResponseSubFlowContextIds getSubFlowContextIdsByFlowIds(RequestSubFlowContextIds requestSubFlowContextIds) throws ErrorException;


}
