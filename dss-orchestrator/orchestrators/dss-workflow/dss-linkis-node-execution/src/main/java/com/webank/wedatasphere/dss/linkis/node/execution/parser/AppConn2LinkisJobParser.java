package com.webank.wedatasphere.dss.linkis.node.execution.parser;

import com.google.gson.reflect.TypeToken;
import com.webank.wedatasphere.dss.linkis.node.execution.conf.LinkisJobExecutionConfiguration;
import com.webank.wedatasphere.dss.linkis.node.execution.entity.AppConn2LinkisPostAction;
import com.webank.wedatasphere.dss.linkis.node.execution.entity.BMLResource;
import com.webank.wedatasphere.dss.linkis.node.execution.exception.LinkisJobExecutionErrorException;
import com.webank.wedatasphere.dss.linkis.node.execution.job.AppConnLinkisJob;
import com.webank.wedatasphere.dss.linkis.node.execution.job.Job;
import com.webank.wedatasphere.dss.linkis.node.execution.job.LinkisJob;
import com.webank.wedatasphere.dss.linkis.node.execution.service.LinkisURLService;
import com.webank.wedatasphere.dss.linkis.node.execution.utils.LinkisJobExecutionUtils;
import com.webank.wedatasphere.dss.linkis.node.execution.utils.LinkisUjesClientUtils;
import org.apache.linkis.httpclient.dws.DWSHttpClient;
import org.apache.linkis.httpclient.dws.config.DWSClientConfig;
import org.apache.linkis.httpclient.response.impl.DefaultHttpResult;

import java.util.HashMap;
import java.util.Map;

public class AppConn2LinkisJobParser extends BML2LinkisJobParser {

    @Override
    public void parseJob(Job job) throws Exception {
        if(!(job instanceof AppConnLinkisJob)) {
            return;
        }
        AppConnLinkisJob appConnLinkisJob = (AppConnLinkisJob) job;
        String runType = appConnLinkisJob.getRunType();
        // 只处理包含 appconn2linkis 的 AppConnLinkisJob，例如：linkis.appconn.<appconnName>.appconn2linkis
        if(!runType.toLowerCase().contains("appconn2linkis")) {
            return;
        }
        job.getLogObj().info(String.format("AppConn %s try to generate Linkis jobContent from AppConn execution.", runType));
        getAndSetCode(null, appConnLinkisJob);
    }

    @Override
    protected void dealExecutionParams(LinkisJob linkisAppConnJob, Map<String, Object> executionParams) {
        String engineType = (String) executionParams.get("engineType");
        String runType = (String) executionParams.get("runType");
        setEngineType(linkisAppConnJob, engineType, runType);
    }

    @Override
    protected Map<String, Object> getExecutionParams(BMLResource bmlResource, LinkisJob job) {
        String user = job.getUser();
        String linkisUrl = LinkisURLService.Factory.getLinkisURLService().getDefaultLinkisURL(job);
        String token = LinkisJobExecutionConfiguration.LINKIS_AUTHOR_USER_TOKEN.getValue(job.getJobProps());
        DWSHttpClient client = null;
        DWSClientConfig clientConfig = LinkisUjesClientUtils.getClientConfig1_X(linkisUrl, user, token, job.getJobProps());
        AppConn2LinkisPostAction appConn2LinkisPostAction = new AppConn2LinkisPostAction();
        appConn2LinkisPostAction.setUrl("/api/rest_j/v1/dss/framework/appconn/execute");
        appConn2LinkisPostAction.addRequestPayload("workspaceStr", job.getRuntimeParams().get("workspace"));
        appConn2LinkisPostAction.addRequestPayload("appConnName", job.getRunType().split("\\.")[0]);
        appConn2LinkisPostAction.addRequestPayload("labels", getLabels(job.getJobProps().get("labels")));
        Map<String, Object> jobContent = new HashMap<>();
        if(job.getCode() != null && !job.getCode().isEmpty()) {
            jobContent.putAll(LinkisJobExecutionUtils.gson.fromJson(job.getCode(), new TypeToken<Map<String, Object>>() {}.getType()));
        }
        jobContent.putAll(job.getParams());
        appConn2LinkisPostAction.addRequestPayload("jobContent", jobContent);
        appConn2LinkisPostAction.setUser(user);
        job.getLogObj().info(String.format("try to ask AppConn %s to execute AppConn2Linkis with requestBody %s.", job.getRunType(), appConn2LinkisPostAction.getRequestPayload()));
        appConn2LinkisPostAction.addHeader("Referer", "");
        try {
            client = new DWSHttpClient(clientConfig, "Workspace-Fetch-Client-");
            DefaultHttpResult result = (DefaultHttpResult) client.execute(appConn2LinkisPostAction);
            if (result.getStatusCode() == 200 || result.getStatusCode() == 0) {
                Map<String, Object> responseBody = LinkisJobExecutionUtils.gson.fromJson(result.getResponseBody(), Map.class);
                return (Map<String, Object>) responseBody.get("data");
            } else {
                throw new LinkisJobExecutionErrorException(50063, "Failed to get workspace str, responseBody is: " +
                        result.getResponseBody());
            }
        } finally {
            if(client != null) {
                client.close();
            }
        }
    }

    private String getLabels(String labels) {
        if (labels.contains("route") || labels.contains("DSSEnv") ) {
            Map<String, Object> labelMap = LinkisJobExecutionUtils.gson.fromJson(labels, Map.class);
            return (String) labelMap.getOrDefault("route", labelMap.getOrDefault("DSSEnv", labels));
        } else {
            return labels;
        }
    }

}
