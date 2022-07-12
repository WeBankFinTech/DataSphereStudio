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

package com.webank.wedatasphere.dss.orchestrator.server.restful;

import com.webank.wedatasphere.dss.appconn.manager.utils.AppConnManagerUtils;
import com.webank.wedatasphere.dss.orchestrator.server.constant.OrchestratorLevelEnum;
import com.webank.wedatasphere.dss.orchestrator.server.entity.request.OrchestratorCreateRequest;
import com.webank.wedatasphere.dss.orchestrator.server.entity.request.OrchestratorDeleteRequest;
import com.webank.wedatasphere.dss.orchestrator.server.entity.request.OrchestratorModifyRequest;
import com.webank.wedatasphere.dss.orchestrator.server.entity.request.OrchestratorRequest;
import com.webank.wedatasphere.dss.orchestrator.server.entity.vo.CommonOrchestratorVo;
import com.webank.wedatasphere.dss.orchestrator.server.service.OrchestratorFrameworkService;
import com.webank.wedatasphere.dss.orchestrator.server.service.OrchestratorService;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.standard.sso.utils.SSOHelper;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.linkis.server.Message;
import org.apache.linkis.server.security.SecurityFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

@RequestMapping(path = "/dss/framework/orchestrator", produces = {"application/json"})
@RestController
public class DSSFrameworkOrchestratorRestful {

    private static final Logger LOGGER = LoggerFactory.getLogger(DSSFrameworkOrchestratorRestful.class);

    @Autowired
    private OrchestratorFrameworkService orchestratorFrameworkService;
    @Autowired
    private OrchestratorService orchestratorService;

    @PostConstruct
    public void init() {
        AppConnManagerUtils.autoLoadAppConnManager();
    }

    /**
     * 创建编排模式
     *
     * @param httpServletRequest
     * @param createRequest
     * @return
     */
    @RequestMapping(path = "createOrchestrator", method = RequestMethod.POST)
    public Message createOrchestrator(HttpServletRequest httpServletRequest, @RequestBody OrchestratorCreateRequest createRequest) throws Exception{
        String username = SecurityFilter.getLoginUsername(httpServletRequest);
        Workspace workspace = SSOHelper.getWorkspace(httpServletRequest);
        LOGGER.info("user {} begin to createOrchestrator, params:{}", username, createRequest);
        //todo 先注释掉
        // orchestratorService.saveOrchestrator(createRequest,null,username);
        CommonOrchestratorVo orchestratorVo = orchestratorFrameworkService.createOrchestrator(username, createRequest, workspace);
        return Message.ok("创建工作流编排模式成功").data("orchestratorId", orchestratorVo.getOrchestratorId());
    }

    /**
     * 查询所有的编排模式
     *
     * @param httpServletRequest
     * @param orchestratorRequest
     * @return
     */
    @RequestMapping(path = "getAllOrchestrator", method = RequestMethod.POST)
    public Message getAllOrchestrator(HttpServletRequest httpServletRequest, @RequestBody OrchestratorRequest orchestratorRequest) {
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
    @RequestMapping(path = "modifyOrchestrator", method = RequestMethod.POST)
    public Message modifyOrchestrator(HttpServletRequest httpServletRequest, @RequestBody OrchestratorModifyRequest modifyRequest) throws Exception{
        String username = SecurityFilter.getLoginUsername(httpServletRequest);
        Workspace workspace = SSOHelper.getWorkspace(httpServletRequest);
        LOGGER.info("user {} begin to modifyOrchestrator, params:{}", username, modifyRequest);
        CommonOrchestratorVo orchestratorVo = orchestratorFrameworkService.modifyOrchestrator(username, modifyRequest, workspace);
        return Message.ok("修改工作流编排模式成功").data("orchestratorId", orchestratorVo.getOrchestratorId());
    }

    /**
     * 删除编排模式
     *
     * @param httpServletRequest
     * @param deleteRequest
     * @return
     */
    @RequestMapping(path = "deleteOrchestrator", method = RequestMethod.POST)
    public Message deleteOrchestrator(HttpServletRequest httpServletRequest, @RequestBody OrchestratorDeleteRequest deleteRequest) throws Exception {
        String username = SecurityFilter.getLoginUsername(httpServletRequest);
        Workspace workspace = SSOHelper.getWorkspace(httpServletRequest);
        LOGGER.info("user {} begin to deleteOrchestrator, params:{}", username, deleteRequest);
        orchestratorFrameworkService.deleteOrchestrator(username, deleteRequest, workspace);
        return Message.ok("删除工作流编排模式成功");
    }

    @RequestMapping(path = "orchestratorLevels", method = RequestMethod.GET)
    public Message getOrchestratorLevels(HttpServletRequest httpServletRequest) {
        String username = SecurityFilter.getLoginUsername(httpServletRequest);
        Workspace workspace = SSOHelper.getWorkspace(httpServletRequest);
        List<OrchestratorLevelEnum> levels = Arrays.asList(OrchestratorLevelEnum.values());
        return Message.ok("获取编排重要级别列表成功").data("orchestratorLevels", levels);
    }
}
