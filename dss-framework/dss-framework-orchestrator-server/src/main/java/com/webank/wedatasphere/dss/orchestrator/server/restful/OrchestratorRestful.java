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
import com.webank.wedatasphere.dss.common.label.DSSLabelUtil;
import com.webank.wedatasphere.dss.common.label.EnvDSSLabel;
import com.webank.wedatasphere.dss.common.label.LabelKeyConvertor;
import com.webank.wedatasphere.dss.common.utils.DSSCommonUtils;
import com.webank.wedatasphere.dss.orchestrator.common.entity.DSSOrchestratorInfo;
import com.webank.wedatasphere.dss.orchestrator.common.entity.OrchestratorVo;
import com.webank.wedatasphere.dss.orchestrator.server.entity.query.QueryOrchestratorVersion;
import com.webank.wedatasphere.dss.orchestrator.server.service.OrchestratorService;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.standard.sso.utils.SSOHelper;
import com.webank.wedatasphere.linkis.server.Message;
import com.webank.wedatasphere.linkis.server.security.SecurityFilter;
import org.codehaus.jackson.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


@Component
@Path("/dss/framework/orchestrator")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrchestratorRestful {
    private final static Logger LOGGER = LoggerFactory.getLogger(OrchestratorRestful.class);
    @Autowired
    OrchestratorService orchestratorService;

    @POST
    @Path("/addOrchestrator")
    public Response addOrchestrator(@Context HttpServletRequest req, JsonNode json) throws Exception {
        String userName = SecurityFilter.getLoginUsername(req);
        String name = json.get("name").getTextValue();
        String workspaceName = json.get("workspaceName").getTextValue();
        String projectName = json.get("projectName").getTextValue();
        String typeStr = json.get("type").getTextValue();
        String desc = json.get("desc").getTextValue();
        Long projectID = json.get("projectID").getLongValue();
//        List<DSSLabel> dssLabelList = Arrays.asList(dssLabels.split(",")).stream().map(label -> {
//            DSSLabel dssLabel = new EnvDSSLabel(label);
//            return dssLabel;
//        }).collect(Collectors.toList());
        List<DSSLabel> dssLabelList = getDSSLabelList(json);
        DSSOrchestratorInfo dssOrchestratorInfo = new DSSOrchestratorInfo();
        dssOrchestratorInfo.setName(name);
        dssOrchestratorInfo.setCreator(userName);
        dssOrchestratorInfo.setDesc(desc);
        dssOrchestratorInfo.setType(typeStr);
        OrchestratorVo orchestratorVo = orchestratorService.createOrchestrator(userName,
                workspaceName,
                projectName,
                projectID,
                desc,
                dssOrchestratorInfo,
                dssLabelList);
        return Message.messageToResponse(Message.ok().data("OrchestratorVo", orchestratorVo));
    }


    @POST
    @Path("/rollbackOrchestrator")
    public Response rollbackOrchestrator(@Context HttpServletRequest request, JsonNode jsonNode) {
        String username = SecurityFilter.getLoginUsername(request);
        Long orchestratorId = jsonNode.get("orchestratorId").getLongValue();
        String version = jsonNode.get("version").getTextValue();
        Long projectId = jsonNode.get("projectId").getLongValue();
        String projectName = jsonNode.get("projectName").getTextValue();
        Workspace workspace = SSOHelper.getWorkspace(request);
        DSSLabel envDSSLabel = getDSSLabelList(jsonNode).get(0);
        try {
            String newVersion = orchestratorService.rollbackOrchestrator(username, projectId, projectName, orchestratorId, version, envDSSLabel, workspace);
            Message message = Message.ok("回滚版本成功").data("newVersion", newVersion);
            return Message.messageToResponse(message);
        } catch (final Throwable t) {
            LOGGER.error("Failed to rollback orchestrator for user {} orchestratorId {}, projectId {} version {}",
                    username, orchestratorId, projectId, version, t);
            return Message.messageToResponse(Message.error("回滚工作流版本失败"));
        }
    }

    @POST
    @Path("/openOrchestrator")
    public Response openOrchestrator(@Context HttpServletRequest req, JsonNode json) throws Exception {
        String openUrl = "";
        String userName = SecurityFilter.getLoginUsername(req);
        List<DSSLabel> dssLabelList = getDSSLabelList(json);
        String workspaceName = json.get("workspaceName").getTextValue();
        Long orchestratorId = json.get("orchestratorId").getLongValue();
        openUrl = orchestratorService.openOrchestrator(userName, workspaceName, orchestratorId, dssLabelList);
        OrchestratorVo orchestratorVo = orchestratorService.getOrchestratorVoById(orchestratorId);
        LOGGER.info("open url is {}, orcId is {}, dssLabels is {}", openUrl, orchestratorId, dssLabelList);
        return Message.messageToResponse(Message.ok().
                data("OrchestratorOpenUrl", openUrl).
                data("OrchestratorVo", orchestratorVo));
    }

    /**
     * 获取编排模式下的所有版本号
     *
     * @param queryOrchestratorVersion
     * @return
     * @throws Exception
     */
    @POST
    @Path("/getVersionByOrchestratorId")
    public Response getVersionByOrchestratorId(@Valid QueryOrchestratorVersion queryOrchestratorVersion) throws Exception {
        List list = orchestratorService.getVersionByOrchestratorId(queryOrchestratorVersion.getOrchestratorId());
        return Message.messageToResponse(Message.ok().data("list", list));
    }

    //生成label list
    public List<DSSLabel> getDSSLabelList(JsonNode json) {
        JsonNode labelJsonNode = json.get(DSSCommonUtils.DSS_LABELS_KEY);
        String dssLabel = labelJsonNode.get(LabelKeyConvertor.ROUTE_LABEL_KEY).getTextValue();
        List<DSSLabel> dssLabelList = Arrays.asList(new EnvDSSLabel(dssLabel));
        return dssLabelList;
    }
}
