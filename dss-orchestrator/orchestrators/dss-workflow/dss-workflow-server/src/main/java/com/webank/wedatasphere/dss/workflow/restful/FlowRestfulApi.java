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

package com.webank.wedatasphere.dss.workflow.restful;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.webank.wedatasphere.dss.appconn.manager.utils.AppConnManagerUtils;
import com.webank.wedatasphere.dss.common.auditlog.OperateTypeEnum;
import com.webank.wedatasphere.dss.common.auditlog.TargetTypeEnum;
import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.common.label.DSSLabel;
import com.webank.wedatasphere.dss.common.label.EnvDSSLabel;
import com.webank.wedatasphere.dss.common.utils.AuditLogUtils;
import com.webank.wedatasphere.dss.common.utils.DSSCommonUtils;
import com.webank.wedatasphere.dss.common.utils.DSSExceptionUtils;
import com.webank.wedatasphere.dss.contextservice.service.ContextService;
import com.webank.wedatasphere.dss.contextservice.service.impl.ContextServiceImpl;
import com.webank.wedatasphere.dss.orchestrator.common.protocol.ResponseConvertOrchestrator;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.standard.sso.utils.SSOHelper;
import com.webank.wedatasphere.dss.workflow.WorkFlowManager;
import com.webank.wedatasphere.dss.workflow.common.entity.DSSFlow;
import com.webank.wedatasphere.dss.workflow.constant.DSSWorkFlowConstant;
import com.webank.wedatasphere.dss.workflow.entity.request.*;
import com.webank.wedatasphere.dss.workflow.entity.vo.ExtraToolBarsVO;
import com.webank.wedatasphere.dss.workflow.lock.DSSFlowEditLockManager;
import com.webank.wedatasphere.dss.workflow.service.DSSFlowService;
import com.webank.wedatasphere.dss.workflow.service.PublishService;
import org.apache.commons.lang.StringUtils;
import org.apache.linkis.server.Message;
import org.apache.linkis.server.security.SecurityFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.*;



@RestController
@RequestMapping(path = "/dss/workflow", produces = {"application/json"})
public class FlowRestfulApi {
    private static final Logger LOGGER = LoggerFactory.getLogger(FlowRestfulApi.class);

    private ContextService contextService = ContextServiceImpl.getInstance();
    @Autowired
    private DSSFlowService flowService;
    @Autowired
    private PublishService publishService;
    @Autowired
    private WorkFlowManager workFlowManager;
    @Autowired
    private HttpServletRequest httpServletRequest;

    @PostConstruct
    public void init() {
        AppConnManagerUtils.autoLoadAppConnManager();
    }

    /**
     * 添加subflow节点
     *

     * @param addFlowRequest
     * @return
     * @throws DSSErrorException
     * @throws JsonProcessingException
     */
    @RequestMapping(value = "addFlow", method = RequestMethod.POST)
    public Message addFlow( @RequestBody AddFlowRequest addFlowRequest) throws DSSErrorException, JsonProcessingException {
        //如果是子工作流，那么分类应该是和父类一起的？
        String userName = SecurityFilter.getLoginUsername(httpServletRequest);
        Workspace workspace = SSOHelper.getWorkspace(httpServletRequest);
        // TODO: 2019/5/23 flowName工程名下唯一校验
        String name = addFlowRequest.getName();
        Long parentFlowID = addFlowRequest.getParentFlowID();
        // 判断parentFlowID中是否已存在名称未name的subflow
        if (flowService.checkExistSameSubflow(parentFlowID, name)){
            return Message.error("子工作流名不能重复");
        }
        String workspaceName = addFlowRequest.getWorkspaceName();
        String projectName = addFlowRequest.getProjectName();
        String version = addFlowRequest.getVersion();
        String description = addFlowRequest.getDescription();
        String uses = addFlowRequest.getUses();
        List<DSSLabel> dssLabelList = new ArrayList<>();
        dssLabelList.add(new EnvDSSLabel(addFlowRequest.getLabels().getRoute()));
        String contextId = contextService.createContextID(workspaceName, projectName, name, version, userName);
        AuditLogUtils.printLog( userName,String.valueOf(workspace.getWorkspaceId()),workspaceName, TargetTypeEnum.CONTEXTID,
               contextId ,name, OperateTypeEnum.CREATE, addFlowRequest);
        DSSFlow dssFlow = workFlowManager.createWorkflow(userName, null, name, contextId, description, parentFlowID,
                uses, new ArrayList<>(), dssLabelList, null, null);
        AuditLogUtils.printLog( userName,workspace.getWorkspaceId(),workspaceName, TargetTypeEnum.WORKFLOW,
                dssFlow.getId() ,name, OperateTypeEnum.CREATE, addFlowRequest);

        // TODO: 2019/5/16 空值校验，重复名校验
        LOGGER.info("User {} end to add flow, name:{}, projectName:{}", userName, name, projectName);
        return Message.ok().data("flow", dssFlow);
    }



    @RequestMapping(value = "publishWorkflow", method = RequestMethod.POST)
    public Message publishWorkflow(@RequestBody PublishWorkflowRequest publishWorkflowRequest) throws Exception {
        Long workflowId = publishWorkflowRequest.getWorkflowId();
        Map<String, Object> labels = new HashMap<>();
        labels.put(EnvDSSLabel.DSS_ENV_LABEL_KEY, publishWorkflowRequest.getLabels().getRoute());
        String comment = publishWorkflowRequest.getComment();
        Workspace workspace = SSOHelper.getWorkspace(httpServletRequest);
        String publishUser = SecurityFilter.getLoginUsername(httpServletRequest);
        return DSSExceptionUtils.getMessage(() -> publishService.submitPublish(publishUser, workflowId, labels, workspace, comment),
                taskId -> {
                if (DSSWorkFlowConstant.PUBLISHING_ERROR_CODE.equals(taskId)) {
                    return Message.error("发布工程已经含有工作流，正在发布中，请稍后再试");
                } else if (StringUtils.isNotEmpty(taskId)) {
                    //发布正常，说明dssflow一定存在，所以不需要判空。
                    DSSFlow dssFlow = flowService.getFlowByID(workflowId);
                    AuditLogUtils.printLog(publishUser,workspace.getWorkspaceId(), workspace.getWorkspaceName(),
                            TargetTypeEnum.WORKFLOW,workflowId, dssFlow.getName(),OperateTypeEnum.PUBLISH, publishWorkflowRequest);
                    return Message.ok("生成工作流发布任务成功").data("releaseTaskId", taskId);
                } else {
                    LOGGER.error("taskId {} is error.", taskId);
                    return Message.error("发布工作流失败");
                }},
                String.format("用户 %s 发布工作流 %s 失败.", publishUser, workflowId));
    }

    /**
     * 获取发布任务状态
     *
     * @param releaseTaskId
     * @return
     */
    @RequestMapping(value = "getReleaseStatus", method = RequestMethod.GET)
    public Message getReleaseStatus(@NotNull(message = "查询的发布id不能为空") @RequestParam(required = false, name = "releaseTaskId") Long releaseTaskId) {
        String username = SecurityFilter.getLoginUsername(httpServletRequest);
        Message message;
        try {
            ResponseConvertOrchestrator response = publishService.getStatus(username, releaseTaskId.toString());
            if (null != response.getResponse()) {
                String status = response.getResponse().getJobStatus().toString();
                status = StringUtils.isNotBlank(status) ? status.toLowerCase() : status;
                //将发布失败原因，返回前端
                if ("failed".equalsIgnoreCase(status)) {
                    message = Message.error("发布失败:" + response.getResponse().getMessage()).data("status", status);
                } else if (StringUtils.isNotBlank(status)) {
                    message = Message.ok("获取进度成功").data("status", status);
                } else {
                    LOGGER.error("status is null or empty, failed to get status");
                    message = Message.error("获取进度失败");
                }
            } else {
                LOGGER.error("status is null or empty, failed to get status");
                message = Message.error("获取进度失败");
            }
        } catch (final Throwable t) {
            LOGGER.error("Failed to get release status for {}", releaseTaskId, t);
            message = Message.error("发布异常:" + t.getMessage());
        }
        return message;
    }


    /**
     * 更新工作流的基本信息，不包括更新Json,BML版本等
     *
     * @param updateFlowBaseInfoRequest
     * @return
     * @throws DSSErrorException
     */

    @RequestMapping(value = "updateFlowBaseInfo", method = RequestMethod.POST)
    public Message updateFlowBaseInfo(@RequestBody UpdateFlowBaseInfoRequest updateFlowBaseInfoRequest) throws DSSErrorException {
        String username = SecurityFilter.getLoginUsername(httpServletRequest);
        Workspace workspace = SSOHelper.getWorkspace(httpServletRequest);
        Long flowID = updateFlowBaseInfoRequest.getId();
        String name = updateFlowBaseInfoRequest.getName();
        String description = updateFlowBaseInfoRequest.getDescription();
        String uses = updateFlowBaseInfoRequest.getUses();
        Long parentFlowID = flowService.getParentFlowID(flowID);
        if (flowService.checkExistSameSubflow(parentFlowID, name)){
            return Message.error("子工作流名不能重复");
        }
        // TODO: 2019/6/13  projectVersionID的更新校验
        //这里可以不做事务
        DSSFlow dssFlow = new DSSFlow();
        dssFlow.setId(flowID);
        dssFlow.setName(name);
        dssFlow.setDescription(description);
        dssFlow.setUses(uses);
        flowService.updateFlowBaseInfo(dssFlow);
        AuditLogUtils.printLog( username,workspace.getWorkspaceId(), workspace.getWorkspaceName(),
                TargetTypeEnum.WORKFLOW,flowID,name,OperateTypeEnum.UPDATE,updateFlowBaseInfoRequest);
        return Message.ok();
    }

    /**
     * 读取工作流的Json数据，提供给前端渲染
     *
     * @param flowID
     * @return
     * @throws DSSErrorException
     */

    @RequestMapping(value = "get", method = RequestMethod.GET)
    public Message get(@RequestParam(required = false, name = "flowId") Long flowID,
                       @RequestParam(required = false, name = "isNotHaveLock") Boolean isNotHaveLock) throws DSSErrorException {
        String username = SecurityFilter.getLoginUsername(httpServletRequest);
        LOGGER.info("User {} start to open workflow {}", username, flowID);
        DSSFlow dssFlow;
        try {
            dssFlow = flowService.getFlow(flowID);
        } catch (NullPointerException e) {
            return Message.error("The workflow is not exists, please check to delete. (打开了不存在的工作流，请确保是否已删除.)");
        }
        if (isNotHaveLock != null && isNotHaveLock) {
            return Message.ok().data("flow", dssFlow);
        }
        Cookie[] cookies = httpServletRequest.getCookies();
        String ticketId = Arrays.stream(cookies).filter(cookie -> DSSWorkFlowConstant.BDP_USER_TICKET_ID.equals(cookie.getName())).findFirst().map(Cookie::getValue).get();
        if (dssFlow != null) {
            // 尝试获取工作流编辑锁
            try {
                //只有父工作流才有锁，子工作流复用父工作流的锁
                if(dssFlow.getRootFlow()) {
                    String flowEditLock = DSSFlowEditLockManager.tryAcquireLock(dssFlow, username, ticketId);
                    dssFlow.setFlowEditLock(flowEditLock);
                }
            } catch (DSSErrorException e) {
                if (DSSWorkFlowConstant.EDIT_LOCK_ERROR_CODE == e.getErrCode()) {
                    return Message.error(e.getDesc());
                }
                throw e;
            }
        }
        return Message.ok().data("flow", dssFlow);
    }

    @RequestMapping(value = "deleteFlow", method = RequestMethod.POST)
    public Message deleteFlow( @RequestBody DeleteFlowRequest deleteFlowRequest) throws DSSErrorException {
        String username = SecurityFilter.getLoginUsername(httpServletRequest);
        Workspace workspace = SSOHelper.getWorkspace(httpServletRequest);
        Long flowID = deleteFlowRequest.getId();
        boolean sure = deleteFlowRequest.getSure() != null && deleteFlowRequest.getSure();
        // TODO: 2019/6/13  projectVersionID的更新校验
        //state为true代表曾经发布过
        DSSFlow dssFlow=flowService.getFlowByID(flowID);
        if(dssFlow==null){
            return Message.error("该工作流已不存在，无法重复删除");
        }
        if (dssFlow.getState() && !sure) {
            return Message.ok().data("warmMsg", "该工作流曾经发布过，删除将会将该工作流的所有版本都删除，是否继续？");
        }
        flowService.batchDeleteFlow(Arrays.asList(flowID));
        AuditLogUtils.printLog( username,workspace.getWorkspaceId(), workspace.getWorkspaceName(),TargetTypeEnum.WORKFLOW,
               flowID, dssFlow.getName(),OperateTypeEnum.DELETE,deleteFlowRequest);
        return Message.ok();
    }

    /**
     * 工作流保存接口，如工作流Json内容有变化，会更新工作流的Json内容
     *
     * @param saveFlowRequest
     * @return
     * @throws DSSErrorException
     * @throws IOException
     */
    @RequestMapping(value = "saveFlow", method = RequestMethod.POST)
    public Message saveFlow( @RequestBody SaveFlowRequest saveFlowRequest) throws DSSErrorException, IOException {
        String username = SecurityFilter.getLoginUsername(httpServletRequest);

        Long flowID = saveFlowRequest.getId();
        String jsonFlow = saveFlowRequest.getJson();
        Workspace workspace = SSOHelper.getWorkspace(httpServletRequest);
        DSSFlow dssFlow = flowService.getFlowByID(flowID);
        String workspaceName = saveFlowRequest.getWorkspaceName();
        String projectName = saveFlowRequest.getProjectName();
        // 判断工作流中是否存在命名相同的节点
        if (checkExistSameFlow(jsonFlow)){
            return Message.error("It exists same flow.(存在相同的节点)");
        }

        Boolean isNotHaveLock = saveFlowRequest.getNotHaveLock();
        String userName = SecurityFilter.getLoginUsername(httpServletRequest);
        String version;
        synchronized (DSSWorkFlowConstant.saveFlowLock.intern(flowID)) {
            if (isNotHaveLock != null && isNotHaveLock.booleanValue()) {
                version = flowService.saveFlow(flowID, jsonFlow, null, userName, workspaceName, projectName);
                AuditLogUtils.printLog( username,workspace.getWorkspaceId(), workspaceName,TargetTypeEnum.WORKFLOW,
                        flowID,dssFlow.getName(),OperateTypeEnum.UPDATE,saveFlowRequest);
                return Message.ok().data("flowVersion", version).data("flowEditLock", null);
            }
            version = flowService.saveFlow(flowID, jsonFlow, null, userName, workspaceName, projectName);
        }
        AuditLogUtils.printLog( username,workspace.getWorkspaceId(), workspaceName,TargetTypeEnum.WORKFLOW,
                flowID,dssFlow.getName(),OperateTypeEnum.UPDATE,saveFlowRequest);
        return Message.ok().data("flowVersion", version);
    }


    /**
     * 工作流编辑锁更新接口
     *
     * @param flowEditLock 老的编辑锁
     * @return 新的编辑锁
     */
    @RequestMapping(value = "/updateFlowEditLock", method = RequestMethod.GET)
    public Message updateFlowEditLock(@RequestParam(required = false, name = "flowEditLock") String flowEditLock) throws DSSErrorException {
        if (StringUtils.isBlank(flowEditLock)) {
            throw new DSSErrorException(60067, "update flowEditLock failed,because flowEditLock is empty");
        }
        return Message.ok().data("flowEditLock", DSSFlowEditLockManager.updateLock(flowEditLock));
    }

    @RequestMapping(value = "/getExtraToolBars", method = RequestMethod.POST)
    public Message getExtraToolBars( @RequestBody GetExtraToolBarsRequest getExtraToolBarsRequest) throws DSSErrorException {
        Workspace workspace = SSOHelper.getWorkspace(httpServletRequest);
        List<ExtraToolBarsVO> barsVOList = flowService.getExtraToolBars(workspace.getWorkspaceId(), getExtraToolBarsRequest.getProjectId());
        return Message.ok().data("extraBars", barsVOList);
    }


    @RequestMapping(value = "/deleteFlowEditLock/{flowEditLock}", method = RequestMethod.POST)
    public Message deleteFlowEditLock(HttpServletRequest req, @PathVariable("flowEditLock") String flowEditLock) throws DSSErrorException {
        DSSFlowEditLockManager.deleteLock(flowEditLock);
        return Message.ok();
    }

    private boolean checkExistSameFlow(String jsonFlow) {
        Map firstLevelMap = DSSCommonUtils.COMMON_GSON.fromJson(jsonFlow, Map.class);
        List<Map> secondLevelList = (List<Map>) firstLevelMap.get("nodes");
        long distinctSize = secondLevelList.stream().map(s -> s.get("title")).distinct().count();
        return distinctSize < secondLevelList.size();
    }

}
