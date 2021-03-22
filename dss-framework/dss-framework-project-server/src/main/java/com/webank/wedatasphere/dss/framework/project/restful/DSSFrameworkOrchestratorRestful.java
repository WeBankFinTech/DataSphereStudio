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

package com.webank.wedatasphere.dss.framework.project.restful;

import com.webank.wedatasphere.dss.framework.common.utils.RestfulUtils;
import com.webank.wedatasphere.dss.framework.project.entity.request.OrchestratorCreateRequest;
import com.webank.wedatasphere.dss.framework.project.entity.request.OrchestratorDeleteRequest;
import com.webank.wedatasphere.dss.framework.project.entity.request.OrchestratorModifyRequest;
import com.webank.wedatasphere.dss.framework.project.entity.request.OrchestratorRequest;
import com.webank.wedatasphere.dss.framework.project.entity.vo.CommonOrchestratorVo;
import com.webank.wedatasphere.dss.framework.project.service.DSSFrameworkOrchestratorService;
import com.webank.wedatasphere.dss.framework.project.service.DSSOrchestratorService;
import com.webank.wedatasphere.dss.orchestrator.common.entity.DSSOrchestratorVersion;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.standard.sso.utils.SSOHelper;
import com.webank.wedatasphere.linkis.server.security.SecurityFilter;
import org.apache.commons.math3.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * created by cooperyang on 2020/9/27
 * Description: 专门用于发布的rest接口
 */
@Component
@Path("/dss/framework/project")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DSSFrameworkOrchestratorRestful {


    private static final Logger LOGGER = LoggerFactory.getLogger(DSSFrameworkOrchestratorRestful.class);

    @Autowired
    private DSSFrameworkOrchestratorService dssFrameworkOrchestratorService;
    @Autowired
    private DSSOrchestratorService orchestratorService;

    /**
     * 创建编排模式
     *
     * @param httpServletRequest
     * @param createRequest
     * @return
     */
    @POST
    @Path("createOrchestrator")
    public Response createOrchestrator(@Context HttpServletRequest httpServletRequest, @Valid OrchestratorCreateRequest createRequest) {
        String username = SecurityFilter.getLoginUsername(httpServletRequest);
        Workspace workspace = SSOHelper.getWorkspace(httpServletRequest);
        LOGGER.info("workspace is {}", workspace.getWorkspaceName());
        try {
            //保存编排模式
            //todo 先注释掉
            // orchestratorService.saveOrchestrator(createRequest,null,username);
            // return RestfulUtils.dealOk("创建工作流编排模式成功", new Pair<>("orchestratorId", 1L));
            CommonOrchestratorVo orchestratorVo = dssFrameworkOrchestratorService.createOrchestrator(username, createRequest, workspace);
            return RestfulUtils.dealOk("创建工作流编排模式成功", new Pair<>("orchestratorId", orchestratorVo.getOrchestratorId()));
        } catch (Exception e) {
            LOGGER.error("Failed to create orchestrator {} for user {}", createRequest, username, e);
            return RestfulUtils.dealError("创建工作流编排模式失败:" + e.getMessage());
        }
    }

    /**
     * 查询所有的编排模式
     *
     * @param httpServletRequest
     * @param orchestratorRequest
     * @return
     */
    @POST
    @Path("getAllOrchestrator")
    public Response getAllOrchestrator(@Context HttpServletRequest httpServletRequest, @Valid OrchestratorRequest orchestratorRequest) {
        try {
            String username = SecurityFilter.getLoginUsername(httpServletRequest);
            return RestfulUtils.dealOk("获取编排模式成功", new Pair<>("page", orchestratorService.getListByPage(orchestratorRequest, username)));
        } catch (Exception e) {
            LOGGER.error("getAllOrchestratorError ", e);
            return RestfulUtils.dealError("获取编排模式失败:" + e.getMessage());
        }
    }

    /**
     * 修改编排模式
     *
     * @param httpServletRequest
     * @param modifyRequest
     * @return
     */
    @POST
    @Path("modifyOrchestrator")
    public Response modifyOrchestrator(@Context HttpServletRequest httpServletRequest, @Valid OrchestratorModifyRequest modifyRequest) {
        String username = SecurityFilter.getLoginUsername(httpServletRequest);
        try {
            //保存编排模式
            orchestratorService.updateOrchestrator(modifyRequest, username);
            return RestfulUtils.dealOk("修改工作流编排模式成功");
            // CommonOrchestratorVo orchestratorVo = dssFrameworkOrchestratorService.createOrchestrator(username, createRequest);
            //return RestfulUtils.dealOk("创建工作流编排模式成功", new Pair<>("orchestratorId", orchestratorVo.getOrchestratorId()));
        } catch (Exception e) {
            LOGGER.error("Failed to create orchestrator {} for user {}", modifyRequest, username, e);
            return RestfulUtils.dealError("创建工作流编排模式失败:" + e.getMessage());
        }
    }

    /**
     * 删除编排模式
     *
     * @param httpServletRequest
     * @param deleteRequest
     * @return
     */
    @POST
    @Path("deleteOrchestrator")
    public Response deleteOrchestrator(@Context HttpServletRequest httpServletRequest, @Valid OrchestratorDeleteRequest deleteRequest) {
        String username = SecurityFilter.getLoginUsername(httpServletRequest);
        try {
            //保存编排模式
            orchestratorService.deleteOrchestrator(deleteRequest, username);
            return RestfulUtils.dealOk("删除工作流编排模式成功");
        } catch (Exception e) {
            LOGGER.error("Failed to delete orchestrator {} for user {}", deleteRequest, username, e);
            return RestfulUtils.dealError("删除工作流编排模式失败:" + e.getMessage());
        }
    }

    @GET
    @Path("/listAllOrchestratorVersions")
    public Response listAllProductFlowVersions(@Context HttpServletRequest request,
                                               @QueryParam("projectId") Long projectId,
                                               @QueryParam("orchestratorId")Long orchestratorId,
                                               @QueryParam("dssLabel")String dssLabel){
        String username = SecurityFilter.getLoginUsername(request);
        try{
            List<DSSOrchestratorVersion> orchestratorVersions = orchestratorService.getOrchestratorVersions(username, projectId, orchestratorId, dssLabel);
            return RestfulUtils.dealOk("获取orchstrator版本信息成功", new Pair<>("orchestratorVersions", orchestratorVersions));
        }catch(final Throwable t){
            LOGGER.error("Failed to list all versions for projectId {}, orchestratorId {} , dssLabel {}", projectId, orchestratorId, dssLabel, t);
            return RestfulUtils.dealError("获取orchestrator版本信息失败");
        }
    }



}
