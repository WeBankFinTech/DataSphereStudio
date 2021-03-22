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

package com.webank.wedatasphere.dss.workflow.restful;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.webank.wedatasphere.dss.common.entity.DSSLabel;
import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.contextservice.service.ContextService;
import com.webank.wedatasphere.dss.contextservice.service.impl.ContextServiceImpl;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.standard.common.desc.CommonDSSLabel;
import com.webank.wedatasphere.dss.standard.sso.utils.SSOHelper;
import com.webank.wedatasphere.dss.workflow.WorkFlowManager;
import com.webank.wedatasphere.dss.workflow.common.entity.DSSFlow;
import com.webank.wedatasphere.dss.workflow.constant.DSSWorkFlowConstant;
import com.webank.wedatasphere.dss.workflow.service.DSSFlowService;
import com.webank.wedatasphere.dss.workflow.service.PublishService;
import com.webank.wedatasphere.linkis.common.exception.ErrorException;
import com.webank.wedatasphere.linkis.server.Message;
import com.webank.wedatasphere.linkis.server.security.SecurityFilter;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.*;

//import static com.webank.wedatasphere.dss.workspace.server.util.DSSWorkspaceConstant.WORKSPACE_ID_STR;

/**
 * Created by v_wbjftang on 2019/5/13.
 */
@Component
@Path("/dss/workflow")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FlowRestfulApi {

    @Autowired
    private DSSFlowService flowService;


    private ContextService contextService = ContextServiceImpl.getInstance();

    @Autowired
    private PublishService publishService;

    @Autowired
    private WorkFlowManager workFlowManager;

    ObjectMapper mapper = new ObjectMapper();


    private static final Logger LOGGER = LoggerFactory.getLogger(FlowRestfulApi.class);

    @POST
    @Path("/addFlow")
//    @ProjectPrivChecker
    public Response addFlow(@Context HttpServletRequest req, JsonNode json) throws DSSErrorException, JsonProcessingException {
        //如果是子工作流，那么分类应该是和父类一起的？
        String userName = SecurityFilter.getLoginUsername(req);
        // TODO: 2019/5/23 flowName工程名下唯一校验
        String name = json.get("name").getTextValue();
        String workspaceName = json.get("workspaceName").getTextValue();
        String projectName = json.get("projectName").getTextValue();
        String version = json.get("version").getTextValue();
        String description = json.get("description") == null ? null : json.get("description").getTextValue();
        Long parentFlowID = json.get("parentFlowID") == null ? null : json.get("parentFlowID").getLongValue();
        String uses = json.get("uses") == null ? null : json.get("uses").getTextValue();
        JsonNode dssLabelsJsonNode = json.get("labels");
        List<DSSLabel> dssLabelList = new ArrayList<>();
        if (dssLabelsJsonNode != null && dssLabelsJsonNode.getElements().hasNext()) {
            Iterator<JsonNode> nodeList = dssLabelsJsonNode.getElements();
            while (nodeList.hasNext()) {
                JsonNode objNode =nodeList.next();
                DSSLabel dssLabel = new CommonDSSLabel(objNode.getTextValue());
                dssLabelList.add(dssLabel);
            }
        }
        String contextId = contextService.createContextID(workspaceName, projectName, name, version, userName);

        DSSFlow dssFlow = workFlowManager.createWorkflow(userName,name,contextId,description,parentFlowID,uses,null,dssLabelList);

        // TODO: 2019/5/16 空值校验，重复名校验
        return Message.messageToResponse(Message.ok().data("flow", dssFlow));
    }


    @POST
    @Path("/publishWorkflow")
    public Response publishWorkflow(@Context HttpServletRequest request, JsonNode jsonNode) throws ErrorException {
        Long workflowId = jsonNode.get("workflowId").getLongValue();
        String dssLabel = jsonNode.get("labels").getTextValue();
        String comment = jsonNode.get("comment").getTextValue();
        Workspace workspace = SSOHelper.getWorkspace(request);
        String publishUser = SecurityFilter.getLoginUsername(request);
        Message message;
        try{
            long taskId = publishService.submitPublish(publishUser, workflowId, dssLabel, workspace);
            LOGGER.info("submit publish task ok ,taskId is {}", taskId);
            if (taskId > 0){
                message = Message.ok("生成工作流发布任务成功").data("releaseTaskId", taskId);
            } else{
                LOGGER.error("taskId {} is error", taskId);
                message = Message.error("发布工作流失败");
            }
        }catch(final Throwable t){
            LOGGER.error("failed to submit publish task for workflow id {}", workflowId, t);
            message = Message.error("发布工作流失败");
        }
        return Message.messageToResponse(message);
    }


    /**
     * 获取发布任务状态
     * @param request
     * @param releaseTaskId
     * @return
     */

    @GET
    @Path("/getReleaseStatus")
    public Response getReleaseStatus(@Context HttpServletRequest request,
                                     @NotNull(message = "查询的发布id不能为空") @QueryParam("releaseTaskId") Long releaseTaskId){
        String username = SecurityFilter.getLoginUsername(request);
        Message message;
        try{
            String status = publishService.getStatus(username, releaseTaskId);
            if (StringUtils.isNotBlank(status)){
                message = Message.ok("获取进度成功").data("status", status);
            }else{
                LOGGER.error("status is null or empty, failed to get status");
                message = Message.error("获取进度失败");
            }
        }catch(final Throwable t){
            LOGGER.error("Failed to get release status for {}", releaseTaskId, t);
            message = Message.error("获取状态失败");
        }
        return Message.messageToResponse(message);
    }


    /**
     * 更新工作流的基本信息，不包括更新Json,BML版本等
     * @param req
     * @param json
     * @return
     * @throws DSSErrorException
     */

    @POST
    @Path("/updateFlowBaseInfo")
//    @ProjectPrivChecker
    public Response updateFlowBaseInfo(@Context HttpServletRequest req, JsonNode json) throws DSSErrorException {
        Long flowID = json.get("id").getLongValue();
        String name = json.get("name") == null ? null : json.get("name").getTextValue();
        String description = json.get("description") == null ? null : json.get("description").getTextValue();
        String uses = json.get("uses") == null ? null : json.get("uses").getTextValue();
//        ioManager.checkeIsExecuting(projectVersionID);
        // TODO: 2019/6/13  projectVersionID的更新校验
        //这里可以不做事务
        DSSFlow dssFlow = new DSSFlow();
        dssFlow.setId(flowID);
        dssFlow.setName(name);
        dssFlow.setDescription(description);
        dssFlow.setUses(uses);
        flowService.updateFlowBaseInfo(dssFlow);
        return Message.messageToResponse(Message.ok());
    }

    /**
     * 读取工作流的Json数据，提供给前端渲染
     * @param req
     * @param flowID
     * @return
     * @throws DSSErrorException
     */

    @GET
    @Path("/get")
    public Response get(@Context HttpServletRequest req, @QueryParam("flowId") Long flowID
    ) throws DSSErrorException {
        // TODO: 2019/5/23 id空值判断
        String username = SecurityFilter.getLoginUsername(req);
        DSSFlow DSSFlow;
        DSSFlow = flowService.getLatestVersionFlow(flowID);
//        if (!username.equals(DSSFlow.getCreator())) {
//            return Message.messageToResponse(Message.ok("不可以访问别人工作流"));
//        }
        return Message.messageToResponse(Message.ok().data("flow", DSSFlow));
    }


//    @GET
//    @Path("/product/get")
//    public Response productGet(@Context HttpServletRequest req, @QueryParam("id") Long flowID) throws ErrorException {
//        DSSFlow DSSFlow;
//        DSSFlow = flowService.getLatestVersionFlow(flowID);
////        dwsFlow=flowService.genBusinessTagForNode(dwsFlow);
//        return Message.messageToResponse(Message.ok().data("flow", DSSFlow));
//    }


    @POST
    @Path("/deleteFlow")
//    @ProjectPrivChecker
    public Response deleteFlow(@Context HttpServletRequest req, JsonNode json) throws DSSErrorException {
        Long flowID = json.get("id").getLongValue();
        boolean sure = json.get("sure") != null && json.get("sure").getBooleanValue();
        // TODO: 2019/6/13  projectVersionID的更新校验
        //state为true代表曾经发布过
        if (flowService.getFlowByID(flowID).getState() && !sure) {
            return Message.messageToResponse(Message.ok().data("warmMsg", "该工作流曾经发布过，删除将会将该工作流的所有版本都删除，是否继续？"));
        }
//        ioManager.checkeIsExecuting(projectVersionID);
        flowService.batchDeleteFlow(Arrays.asList(flowID));
        return Message.messageToResponse(Message.ok());
    }

    /**
     * 工作流保存接口，如工作流Json内容有变化，会更新工作流的Json内容
     * @param req
     * @param json
     * @return
     * @throws DSSErrorException
     * @throws IOException
     */

    @POST
    @Path("/saveFlow")
//    @ProjectPrivChecker
    public Response saveFlow(@Context HttpServletRequest req, JsonNode json) throws DSSErrorException, IOException {
        Long flowID = json.get("id").getLongValue();
        String jsonFlow = json.get("json").getTextValue();
        String workspaceName = json.get("workspaceName").getTextValue();
        String projectName = json.get("projectName").getTextValue();
        String userName = SecurityFilter.getLoginUsername(req);
        //String comment = json.get("comment") == null?"保存更新":json.get("comment").getTextValue();
        //目前保存comment不使用了,发布才有comment
        String comment = null;
//        ioManager.checkeIsExecuting(projectVersionID);
        // TODO: 2020/6/9 为了cs和bml，加锁
        String version = null;
        String newFlowEditLock = null;
        synchronized (DSSWorkFlowConstant.saveFlowLock.intern(flowID)) {
            version = flowService.saveFlow(flowID, jsonFlow, comment, userName, workspaceName, projectName);
        }
        return Message.messageToResponse(Message.ok().data("flowVersion", version).data("flowEditLock", newFlowEditLock));
    }

}
