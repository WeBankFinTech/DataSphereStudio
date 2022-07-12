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

import com.webank.wedatasphere.dss.common.label.DSSLabel;
import com.webank.wedatasphere.dss.common.label.EnvDSSLabel;
import com.webank.wedatasphere.dss.orchestrator.common.entity.OrchestratorVo;
import com.webank.wedatasphere.dss.orchestrator.server.entity.request.OpenOrchestratorRequest;
import com.webank.wedatasphere.dss.orchestrator.server.entity.request.QueryOrchestratorVersion;
import com.webank.wedatasphere.dss.orchestrator.server.entity.request.RollbackOrchestratorRequest;
import com.webank.wedatasphere.dss.orchestrator.server.service.OrchestratorService;
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
import java.util.Arrays;
import java.util.List;


@RequestMapping(path = "/dss/framework/orchestrator", produces = {"application/json"})
@RestController
public class OrchestratorRestful {
    private final static Logger LOGGER = LoggerFactory.getLogger(OrchestratorRestful.class);
    @Autowired
    OrchestratorService orchestratorService;

    @RequestMapping(path = "rollbackOrchestrator", method = RequestMethod.POST)
    public Message rollbackOrchestrator(HttpServletRequest request, @RequestBody RollbackOrchestratorRequest rollbackOrchestratorRequest) {
        String username = SecurityFilter.getLoginUsername(request);
        Long orchestratorId = rollbackOrchestratorRequest.getOrchestratorId();
        String version = rollbackOrchestratorRequest.getVersion();
        Long projectId = rollbackOrchestratorRequest.getProjectId();
        String projectName = rollbackOrchestratorRequest.getProjectName();
        Workspace workspace = SSOHelper.getWorkspace(request);
        DSSLabel envDSSLabel = new EnvDSSLabel(rollbackOrchestratorRequest.getLabels().getRoute());
        try {
            LOGGER.info("user {} begin to rollbackOrchestrator, params:{}", username, rollbackOrchestratorRequest);
            String newVersion = orchestratorService.rollbackOrchestrator(username, projectId, projectName, orchestratorId, version, envDSSLabel, workspace);
            Message message = Message.ok("回滚版本成功").data("newVersion", newVersion);
            return message;
        } catch (final Throwable t) {
            LOGGER.error("Failed to rollback orchestrator for user {} orchestratorId {}, projectId {} version {}",
                    username, orchestratorId, projectId, version, t);
            return Message.error("回滚工作流版本失败");
        }
    }

    @RequestMapping(path = "openOrchestrator", method = RequestMethod.POST)
    public Message openOrchestrator(HttpServletRequest req, @RequestBody OpenOrchestratorRequest openOrchestratorRequest) throws Exception {
        String openUrl = "";
        String userName = SecurityFilter.getLoginUsername(req);
        Workspace workspace = SSOHelper.getWorkspace(req);
        List<DSSLabel> dssLabelList = Arrays.asList(new EnvDSSLabel(openOrchestratorRequest.getLabels().getRoute()));
        Long orchestratorId = openOrchestratorRequest.getOrchestratorId();
        LOGGER.info("user {} try to openOrchestrator, params:{}", userName, openOrchestratorRequest);
        openUrl = orchestratorService.openOrchestrator(userName, workspace, orchestratorId, dssLabelList);
        OrchestratorVo orchestratorVo = orchestratorService.getOrchestratorVoById(orchestratorId);
        LOGGER.info("open url is {}, orcId is {}, dssLabels is {}", openUrl, orchestratorId, dssLabelList);
        return Message.ok().data("OrchestratorOpenUrl", openUrl).
                data("OrchestratorVo", orchestratorVo);
    }

    /**
     * 获取编排模式下的所有版本号
     *
     * @param queryOrchestratorVersion
     * @return
     * @throws Exception
     */
    @RequestMapping(path = "getVersionByOrchestratorId", method = RequestMethod.POST)
    public Message getVersionByOrchestratorId(@RequestBody QueryOrchestratorVersion queryOrchestratorVersion) throws Exception {
        List list = orchestratorService.getVersionByOrchestratorId(queryOrchestratorVersion.getOrchestratorId());
        return Message.ok().data("list", list);
    }
}
