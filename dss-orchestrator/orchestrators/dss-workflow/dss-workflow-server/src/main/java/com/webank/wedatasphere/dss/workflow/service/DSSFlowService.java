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

package com.webank.wedatasphere.dss.workflow.service;

import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.common.label.DSSLabel;
import com.webank.wedatasphere.dss.common.label.LabelRouteVO;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.workflow.common.entity.DSSFlow;
import com.webank.wedatasphere.dss.workflow.entity.DSSFlowName;
import com.webank.wedatasphere.dss.workflow.entity.NodeInfo;
import com.webank.wedatasphere.dss.workflow.entity.request.*;
import com.webank.wedatasphere.dss.workflow.entity.response.*;
import com.webank.wedatasphere.dss.workflow.entity.vo.ExtraToolBarsVO;
import org.apache.linkis.common.exception.ErrorException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface DSSFlowService {
    DSSFlow getFlowByID(Long id);

    DSSFlow getFlowWithJsonAndSubFlowsByID(Long rootFlowId);

    DSSFlow addFlow(DSSFlow dssFlow, String contextIdStr, String orcVersion, String schedulerAppConn) throws DSSErrorException;

    DSSFlow addSubFlow(DSSFlow dssFlow, Long parentFlowId, String contextIdStr, String orcVersion, String schedulerAppConn) throws DSSErrorException;

    /**
     * 通过flowID获取最新版本的dwsFlow，版本信息在latestVersion
     *
     * @return
     */
    DSSFlow getFlow(Long flowId);

    List<String> getSubFlowContextIdsByFlowIds(List<Long> flowIdList) throws ErrorException;

    void updateFlowBaseInfo(DSSFlow dssFlow) throws DSSErrorException;

    void batchDeleteFlow(List<Long> flowIdlist);

    String saveFlow(Long flowId,
                    String jsonFlow,
                    String comment,
                    String userName,
                    String workspaceName,
                    String projectName,
                    LabelRouteVO labels
    ) throws Exception;

    void batchEditFlow(BatchEditFlowRequest batchEditFlowRequest, String ticketId, Workspace workspace, String userName) throws Exception;

    void updateTOSaveStatus(Long projectId, Long flowID, Long orchestratorId) throws Exception;

    void deleteFlowMetaData(Long orchestratorId);

    void saveAllFlowMetaData(Long flowId, Long orchestratorId);

    void saveFlowMetaData(Long flowID, String jsonFlow, Long orchestratorId);

    DSSFlow copyRootFlow(Long rootFlowId, String userName, Workspace workspace,
                         String projectName, String version, String contextIdStr,
                         String description, List<DSSLabel> dssLabels,String nodeSuffix,
                         String newFlowName, Long newProjectId) throws DSSErrorException, IOException;

    Long getParentFlowID(Long id);

    List<ExtraToolBarsVO> getExtraToolBars(long workspaceId, long projectId);

    boolean checkExistSameSubflow(Long parentFlowID, String name);

    boolean checkExistSameFlow(Long parentFlowID, String name, String existName);

    boolean checkIsExistSameFlow(String jsonFlow);

    List<String> checkIsSave(Long parentFlowID, String jsonFlow);

    String getFlowJson(String userName, String projectName, DSSFlow dssFlow);

    DataDevelopNodeResponse queryDataDevelopNodeList(String username, Workspace workspace, DataDevelopNodeRequest request);

    Map<String,Object> getDataDevelopNodeContent(String nodeId, Long contentId) throws DSSErrorException;

    DataViewNodeResponse queryDataViewNode(String username, Workspace workspace, DataViewNodeRequest request);

    List<NodeInfo> getNodeInfoByGroupName(String groupNameEn);

    DSSFlowName queryFlowNameList(String username, Workspace workspace, String groupNameEn,String nodeTypeName);

    List<String> queryViewId(Workspace workspace,String username);

    DataCheckerNodeResponse queryDataCheckerNode(String username, Workspace workspace, DataCheckerNodeRequest request);

    EventSenderNodeResponse queryEventSenderNode(String username, Workspace workspace, EventSenderNodeRequest request);

    EventReceiveNodeResponse queryEventReceiveNode(String username, Workspace workspace, EventReceiverNodeRequest request);

    List<String> queryJobDesc(Workspace workspace, String username);

    List<String> querySourceType(Workspace workspace, String username);

    void getAllOldFlowId(Long flowId, List<Long> flowIdList);

    void deleteNodeContent(List<Long> flowIdList);

    void getRootFlowProxy(DSSFlow dssFlow);
}
