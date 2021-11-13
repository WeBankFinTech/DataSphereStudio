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

package com.webank.wedatasphere.dss.framework.project.restful;

import com.webank.wedatasphere.dss.framework.project.dao.DSSProjectMapper;
import com.webank.wedatasphere.dss.framework.project.entity.request.OrchestratorCreateRequest;
import com.webank.wedatasphere.dss.framework.project.entity.request.OrchestratorDeleteRequest;
import com.webank.wedatasphere.dss.framework.project.entity.request.OrchestratorModifyRequest;
import com.webank.wedatasphere.dss.framework.project.entity.request.OrchestratorRequest;
import com.webank.wedatasphere.dss.framework.project.entity.vo.CommonOrchestratorVo;
import com.webank.wedatasphere.dss.framework.project.service.DSSFrameworkOrchestratorService;
import com.webank.wedatasphere.dss.framework.project.service.DSSOrchestratorService;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.standard.sso.utils.SSOHelper;
import org.apache.linkis.server.Message;
import org.apache.linkis.server.security.SecurityFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;


@RequestMapping(path = "/dss/framework/project", produces = {"application/json"})
@RestController
public class DSSFrameworkOrchestratorRestful {

    private static final Logger LOGGER = LoggerFactory.getLogger(DSSFrameworkOrchestratorRestful.class);

    @Autowired
    private DSSFrameworkOrchestratorService dssFrameworkOrchestratorService;
    @Autowired
    private DSSOrchestratorService orchestratorService;

    @Autowired
    private DSSProjectMapper projectMapper;

    /**
     * 创建编排模式
     *
     * @param httpServletRequest
     * @param createRequest
     * @return
     */
    @RequestMapping(path ="createOrchestrator", method = RequestMethod.POST)
    public Message createOrchestrator(@Context HttpServletRequest httpServletRequest, @RequestBody OrchestratorCreateRequest createRequest) {
        String username = SecurityFilter.getLoginUsername(httpServletRequest);
        Workspace workspace = SSOHelper.getWorkspace(httpServletRequest);
        LOGGER.info("workspace is {}", workspace.getWorkspaceName());
        try {
            //保存编排模式
            //todo 先注释掉
            // orchestratorService.saveOrchestrator(createRequest,null,username);
            // return RestfulUtils.dealOk("创建工作流编排模式成功", new Pair<>("orchestratorId", 1L));
            CommonOrchestratorVo orchestratorVo = dssFrameworkOrchestratorService.createOrchestrator(username, createRequest, workspace);
            return Message.ok("创建工作流编排模式成功").data("orchestratorId", orchestratorVo.getOrchestratorId());
        } catch (Exception e) {
            LOGGER.error("Failed to create orchestrator {} for user {}", createRequest, username, e);
            return Message.error("创建工作流编排模式失败:" + e.getMessage());
        }
    }

    /**
     * 查询所有的编排模式
     *
     * @param httpServletRequest
     * @param orchestratorRequest
     * @return
     */
    @RequestMapping(path ="getAllOrchestrator", method = RequestMethod.POST)
    public Message getAllOrchestrator(@Context HttpServletRequest httpServletRequest, @RequestBody OrchestratorRequest orchestratorRequest) {
        try {
            String username = SecurityFilter.getLoginUsername(httpServletRequest);
            return Message.ok("获取编排模式成功").data("page", orchestratorService.getListByPage(orchestratorRequest, username));
        } catch (Exception e) {
            LOGGER.error("getAllOrchestratorError ", e);
            return Message.error("获取编排模式失败:" + e.getMessage());
        }
    }

    /**
     * 修改编排模式
     *
     * @param httpServletRequest
     * @param modifyRequest
     * @return
     */
    @RequestMapping(path ="modifyOrchestrator", method = RequestMethod.POST)
    public Message modifyOrchestrator(@Context HttpServletRequest httpServletRequest, @RequestBody OrchestratorModifyRequest modifyRequest) {
        String username = SecurityFilter.getLoginUsername(httpServletRequest);
        Workspace workspace = SSOHelper.getWorkspace(httpServletRequest);
        LOGGER.info("workspace is {}", workspace.getWorkspaceName());
        try {
             CommonOrchestratorVo orchestratorVo = dssFrameworkOrchestratorService.modifyOrchestrator(username, modifyRequest,workspace);
            //保存编排模式
//            orchestratorService.updateOrchestrator(modifyRequest, username);
            return Message.ok("创建工作流编排模式成功").data("orchestratorId", orchestratorVo.getOrchestratorId());
        } catch (Exception e) {
            LOGGER.error("Failed to create orchestrator {} for user {}", modifyRequest, username, e);
            return Message.error("创建工作流编排模式失败:" + e.getMessage());
        }
    }

    /**
     * 删除编排模式
     *
     * @param httpServletRequest
     * @param deleteRequest
     * @return
     */
    @RequestMapping(path ="deleteOrchestrator", method = RequestMethod.POST)
    public Message deleteOrchestrator(@Context HttpServletRequest httpServletRequest, @RequestBody OrchestratorDeleteRequest deleteRequest) {
        String username = SecurityFilter.getLoginUsername(httpServletRequest);
        Workspace workspace = SSOHelper.getWorkspace(httpServletRequest);
        LOGGER.info("workspace is {}", workspace.getWorkspaceName());
        try {
            dssFrameworkOrchestratorService.deleteOrchestrator(username, deleteRequest,workspace);
            return Message.ok("删除工作流编排模式成功");
        } catch (Exception e) {
            LOGGER.error("Failed to delete orchestrator {} for user {}", deleteRequest, username, e);
            return Message.error("删除工作流编排模式失败:" + e.getMessage());
        }
    }
}
