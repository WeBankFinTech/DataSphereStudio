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

package com.webank.wedatasphere.dss.workflow.service.impl;

import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.common.protocol.framework.ReleaseOrchestratorRequest;
import com.webank.wedatasphere.dss.common.protocol.framework.ReleaseOrchestratorResponse;
import com.webank.wedatasphere.dss.common.protocol.framework.ReleaseOrchestratorStatusRequest;
import com.webank.wedatasphere.dss.common.protocol.framework.ReleaseOrchestratorStatusResponse;
import com.webank.wedatasphere.dss.common.utils.DSSCommonUtils;
import com.webank.wedatasphere.dss.common.utils.DSSExceptionUtils;
import com.webank.wedatasphere.dss.orchestrator.common.protocol.RequestOrcInfo;
import com.webank.wedatasphere.dss.orchestrator.common.protocol.ResponseOrcInfo;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.workflow.service.PublishService;
import com.webank.wedatasphere.linkis.common.conf.CommonVars;
import com.webank.wedatasphere.linkis.rpc.Sender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * created by cooperyang on 2021/1/21
 * Description:
 */
@Service
public class PublishServiceImpl implements PublishService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PublishServiceImpl.class);


    //todo 先写死 以后按照label来

    private static final String RELEASE_SERVER_NAME =
            CommonVars.apply("wds.dss.release.sever.name", "dss-framework-project-server").getValue();

    private static final String ORCHESTRATOR_SERVER_NAME =
            CommonVars.apply("wds.dss.orchestrator.sever.name", "dss-framework-orchestrator-server-dev").getValue();

    private static final boolean IS_DIRECT = CommonVars.apply("wds.dss.env.is.direct", true).getValue();

    private final Sender releaseSender = Sender.getSender(RELEASE_SERVER_NAME);

    private final Sender orchestratorSender = Sender.getSender(ORCHESTRATOR_SERVER_NAME);


    @Override
    public long submitPublish(String publishUser, Long workflowId, String dssLabel, Workspace workspace) throws Exception {
        LOGGER.info("User {} begins to publish workflow {}", publishUser, workflowId);
        //1 获取对应的orcId 和 orcVersionId
        //2.进行提交
        RequestOrcInfo requestOrcInfo = new RequestOrcInfo(workflowId, publishUser, dssLabel);
        try {
            ResponseOrcInfo responseOrcInfo = (ResponseOrcInfo) orchestratorSender.ask(requestOrcInfo);
            LOGGER.info("end to publish workflow orcId is {}, orcVersionId is {}", responseOrcInfo.getOrchestratorId(),
                    responseOrcInfo.getOrchestratorVersionId());
            ReleaseOrchestratorRequest releaseOrchestratorRequest = new ReleaseOrchestratorRequest(publishUser, responseOrcInfo.getOrchestratorVersionId(),
                    responseOrcInfo.getOrchestratorId(), dssLabel, workspace.getWorkspaceName(), DSSCommonUtils.COMMON_GSON.toJson(workspace));
            ReleaseOrchestratorResponse response = (ReleaseOrchestratorResponse)releaseSender.ask(releaseOrchestratorRequest);
            return response.jobId();
        } catch (final Throwable t) {
            LOGGER.error("failed to submit publish {} ", workflowId, t);
            DSSExceptionUtils.dealErrorException(63325, "failed to submit publish " + workflowId, t, DSSErrorException.class);
        }
        return -1L;
    }

    @Override
    public String getStatus(String username, Long releaseTaskId) {
        if (LOGGER.isDebugEnabled()){
            LOGGER.debug("{} is asking status of {}", username, releaseTaskId);
        }
        //通过rpc的方式去获取到最新status
        ReleaseOrchestratorStatusRequest releaseOrchestratorStatusRequest = new ReleaseOrchestratorStatusRequest(releaseTaskId);
        ReleaseOrchestratorStatusResponse releaseOrchestratorStatusResponse = (ReleaseOrchestratorStatusResponse)releaseSender.ask(releaseOrchestratorStatusRequest);
        LOGGER.info("user {} gets status of {}, status is {}", username, releaseTaskId, releaseOrchestratorStatusResponse.status());
        return releaseOrchestratorStatusResponse.status();
    }
}
