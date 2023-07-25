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

package com.webank.wedatasphere.dss.linkis.node.execution.job;

import com.webank.wedatasphere.dss.linkis.node.execution.conf.LinkisJobExecutionConfiguration;
import com.webank.wedatasphere.dss.linkis.node.execution.entity.WorkspaceInfoGetAction;
import com.webank.wedatasphere.dss.linkis.node.execution.exception.LinkisJobExecutionErrorException;
import com.webank.wedatasphere.dss.linkis.node.execution.service.LinkisURLService;
import com.webank.wedatasphere.dss.linkis.node.execution.utils.LinkisJobExecutionUtils;
import com.webank.wedatasphere.dss.linkis.node.execution.utils.LinkisUjesClientUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.linkis.common.utils.JsonUtils;
import org.apache.linkis.httpclient.dws.DWSHttpClient;
import org.apache.linkis.httpclient.dws.config.DWSClientConfig;
import org.apache.linkis.httpclient.response.impl.DefaultHttpResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;


public abstract class Builder {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    protected abstract String getJobType();

    protected abstract LinkisJob creatLinkisJob(boolean isLinkisType);

    protected abstract void fillJobInfo(Job job);

    protected abstract void fillLinkisJobInfo(LinkisJob linkisJob);

    protected abstract void fillCommonLinkisJobInfo(CommonLinkisJob linkisAppConnJob);


    public Job build() throws Exception {

        LinkisJob job = null;
        String jobType = getJobType();
        String[] jobTypeSplit = jobType.split("\\.");
        if (jobTypeSplit.length < 2) {
            throw new LinkisJobExecutionErrorException(90100, "This is not Linkis job type, this jobtype is " + jobType);
        }
        String engineType = jobTypeSplit[1];
        //delete linkis.engineType
        String runType = StringUtils.substringAfterLast(jobType, jobTypeSplit[0] + "." + jobTypeSplit[1] + ".");

        if (LinkisJobExecutionConfiguration.LINKIS_CONTROL_EMPTY_NODE.equalsIgnoreCase(jobType)) {
            job = new AbstractCommonLinkisJob() {
                @Override
                public String getSubmitUser() {
                    return null;
                }

                @Override
                public String getUser() {
                    return null;
                }

                @Override
                public String getJobName() {
                    return null;
                }
            };

            job.setJobType(JobTypeEnum.EmptyJob);
            return job;
        }
        if (LinkisJobExecutionUtils.isCommonAppConnJob(engineType)) {
            job = creatLinkisJob(false);
            job.setJobType(JobTypeEnum.CommonJob);
        } else {
            job = creatLinkisJob(true);
            job.setJobType(JobTypeEnum.CommonJob);
            fillCommonLinkisJobInfo((CommonLinkisJob) job);
        }

        job.setEngineType(engineType);
        job.setRunType(runType);
        fillJobInfo(job);
        fillLinkisJobInfo(job);

        if (job.getRuntimeParams() == null) {
            job.setRuntimeParams(new HashMap<>());
        }
        String contextId = getContextID(job);
        if (StringUtils.isBlank(contextId)) {
            throw new LinkisJobExecutionErrorException(90100, "contextID is not exists.");
        }
        contextId = contextId.replace("/", "\\");
        Map contextMap = JsonUtils.jackson().readValue(contextId, Map.class);
        logger.info("the contextMap is:{}", contextMap);
        String workspaceName = (String) JsonUtils.jackson().readValue((String) contextMap.get("value"), Map.class).get("workspace");
        logger.info("try to get workspace str by workspaceName {}.", workspaceName);
        String workspace = getWorkspaceStr(job, workspaceName);
        logger.info("Got workspace str {}.", workspace);
        job.getRuntimeParams().put("contextID", contextId);
        job.getRuntimeParams().put("workspace", workspace);
        return job;
    }

    protected abstract String getContextID(Job job);

    private String getWorkspaceStr(Job job, String workspaceName) throws Exception {
        String user = job.getUser();
        String linkisUrl = LinkisURLService.Factory.getLinkisURLService().getDefaultLinkisURL(job);
        String token = LinkisJobExecutionConfiguration.LINKIS_AUTHOR_USER_TOKEN.getValue(job.getJobProps());
        DWSHttpClient client = null;
        DWSClientConfig clientConfig = LinkisUjesClientUtils.getClientConfig1_X(linkisUrl, user, token, job.getJobProps());
        WorkspaceInfoGetAction workspaceInfoGetAction = new WorkspaceInfoGetAction();
        workspaceInfoGetAction.setURL("/api/rest_j/v1/dss/framework/workspace/getWorkSpaceStr");
        workspaceInfoGetAction.setParameter("workspaceName", workspaceName);
        workspaceInfoGetAction.setUser(user);
        workspaceInfoGetAction.addHeader("Referer", "");
        try {
            client = new DWSHttpClient(clientConfig, "Workspace-Fetch-Client-");
            DefaultHttpResult result = (DefaultHttpResult) client.execute(workspaceInfoGetAction);
            if (result.getStatusCode() == 200 || result.getStatusCode() == 0) {
                Map<String, Object> responseBody = JsonUtils.jackson().readValue(result.getResponseBody(), Map.class);
                Map<String, Object> data = (Map<String, Object>) responseBody.get("data");
                return (String) data.get("workspaceStr");
            } else {
                throw new LinkisJobExecutionErrorException(50063, "Failed to get workspace str, responseBody is: " +
                        result.getResponseBody());
            }
        } finally {
            IOUtils.closeQuietly(client);
        }
    }

}
