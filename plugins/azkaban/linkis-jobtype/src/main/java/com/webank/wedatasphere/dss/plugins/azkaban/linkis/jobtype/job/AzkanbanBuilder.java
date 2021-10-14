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

package com.webank.wedatasphere.dss.plugins.azkaban.linkis.jobtype.job;

import com.google.gson.Gson;
import com.webank.wedatasphere.dss.linkis.node.execution.conf.LinkisJobExecutionConfiguration;
import com.webank.wedatasphere.dss.linkis.node.execution.entity.BMLResource;
import com.webank.wedatasphere.dss.linkis.node.execution.job.Builder;
import com.webank.wedatasphere.dss.linkis.node.execution.job.CommonLinkisJob;
import com.webank.wedatasphere.dss.linkis.node.execution.job.Job;
import com.webank.wedatasphere.dss.linkis.node.execution.job.LinkisJob;
import com.webank.wedatasphere.dss.linkis.node.execution.service.LinkisURLService;
import com.webank.wedatasphere.dss.linkis.node.execution.utils.LinkisJobExecutionUtils;
import com.webank.wedatasphere.dss.plugins.azkaban.linkis.jobtype.conf.LinkisJobTypeConf;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AzkanbanBuilder extends Builder{

    private static final Logger LOGGER = LoggerFactory.getLogger(AzkanbanBuilder.class);

    private Map<String, String> jobProps;

    public AzkanbanBuilder setJobProps(Map<String, String> jobProps) {
        this.jobProps = jobProps;
        return this;
    }

    @Override
    protected String getJobType() {
        return jobProps.getOrDefault(LinkisJobExecutionConfiguration.LINKIS_TYPE,
                LinkisJobExecutionConfiguration.LINKIS_DEFAULT_TYPE.getValue(jobProps));
    }

    @Override
    protected LinkisJob creatLinkisJob(boolean isLinkisType) {
        if(isLinkisType){
            AzkabanCommonLinkisJob linkisJob = new AzkabanCommonLinkisJob();
            linkisJob.setJobProps(this.jobProps);
            return linkisJob;
        } else {
            AzkabanAppConnLinkisJob linkisJob = new AzkabanAppConnLinkisJob();
            linkisJob.setJobProps(this.jobProps);
            return linkisJob;
        }
    }

    @Override
    protected void fillJobInfo(Job job) {
        job.setCode(jobProps.get(LinkisJobTypeConf.COMMAND));

        Map<String, Object> params = new HashMap<>();
        if(jobProps.containsKey("run_date")){
            params.put("run_date", jobProps.get("run_date"));
        }
        job.setParams(params);

        Map<String, Object> runtimeMap = new HashMap<>();
        if (null != job.getRuntimeParams()){
            runtimeMap = job.getRuntimeParams();
        }
        //update by peaceWong add contextID to runtimeMap
        if (jobProps.containsKey(LinkisJobExecutionConfiguration.FLOW_CONTEXTID)){
            runtimeMap.put("contextID", jobProps.get(LinkisJobExecutionConfiguration.FLOW_CONTEXTID).replace("/", "\\"));
        }
        runtimeMap.put("nodeName", jobProps.get(LinkisJobTypeConf.JOB_ID));

        runtimeMap.put(LinkisJobTypeConf.DSS_LABELS_KEY, jobProps.get(LinkisJobTypeConf.DSS_LABELS_KEY));
        //to put a workspace for linkis job
        String workspace = "";
        try {
            workspace = getWorkspace(job.getUser(), job);
        } catch (Exception e) {
           LOGGER.error("Failed to get workspace", e);
        }
        runtimeMap.put("workspace", workspace);
        job.setRuntimeParams(runtimeMap);
    }

    private String getWorkspace(String user, Job job) throws Exception {
        String linkisUrl = LinkisURLService.Factory.getLinkisURLService().getDefaultLinkisURL(job);
        String token = LinkisJobExecutionConfiguration.LINKIS_AUTHOR_USER_TOKEN.getValue(job.getJobProps());
        String dssUrl = linkisUrl.endsWith("/") ? linkisUrl + "api/rest_j/v1/dss/framework/project/getWorkSpaceStr"
                : linkisUrl + "/api/rest_j/v1/dss/framework/project/getWorkSpaceStr";
        HttpGet httpGet = new HttpGet(dssUrl);
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("Token-User", user));
        params.add(new BasicNameValuePair("Token-Code", token));
        httpGet.addHeader("Token-User", user);
        httpGet.addHeader("Token-Code", token);
        CookieStore cookieStore = new BasicCookieStore();
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        HttpClientContext context;
        String responseContent;
        try {
            httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
            context = HttpClientContext.create();
            response = httpClient.execute(httpGet, context);
            HttpEntity entity = response.getEntity();
            responseContent = EntityUtils.toString(entity,"utf-8");
            Gson gson = new Gson();
            Map resultMap = gson.fromJson(responseContent, Map.class);
            Object obj = resultMap.get("data");
            if (obj instanceof Map){
                if (null != ((Map) obj).get("workspaceStr")){
                    return ((Map) obj).get("workspaceStr").toString();
                }else {
                    return "";
                }
            }
        } finally {
            IOUtils.closeQuietly(response);
            IOUtils.closeQuietly(httpClient);
        }
        return "";
    }


    @Override
    protected void fillLinkisJobInfo(LinkisJob linkisJob) {
        linkisJob.setConfiguration(findConfiguration(LinkisJobExecutionConfiguration.NODE_CONF_PREFIX));
        Map<String, Object> variables = findVariables(LinkisJobExecutionConfiguration.FLOW_VARIABLE_PREFIX);
        //只有工作流参数中没有设置,我们才会去进行替换
        if(jobProps.containsKey("run_date") && !variables.containsKey("run_date")){
            variables.put("run_date", jobProps.get("run_date"));
        }
        linkisJob.setVariables(variables);
        linkisJob.setSource(getSource());
    }

    @Override
    protected void fillCommonLinkisJobInfo(CommonLinkisJob linkisAppConnJob) {
        linkisAppConnJob.setJobResourceList(LinkisJobExecutionUtils.getResourceListByJson(jobProps.get("resources")));

        String projectResourceName = LinkisJobExecutionConfiguration.PROJECT_PREFIX + "."
                + jobProps.get(LinkisJobTypeConf.PROJECT_NAME) + LinkisJobExecutionConfiguration.RESOURCES_NAME;
        linkisAppConnJob.setProjectResourceList(LinkisJobExecutionUtils.getResourceListByJson(jobProps.get(projectResourceName)));

        linkisAppConnJob.setFlowNameAndResources(findFLowNameAndResources());
    }


    private Map<String, String> getSource() {
        Map<String, String> source = new HashMap<>();
        source.put("projectName", jobProps.get(LinkisJobTypeConf.PROJECT_NAME));
        source.put("flowName", jobProps.get(LinkisJobTypeConf.FLOW_NAME));
        source.put("nodeName", jobProps.get(LinkisJobTypeConf.JOB_ID));
        return source;
    }

    /**
     * Looking for custom variables through the corresponding prefix
     * For example, flow.variable.a=test returns map{a->test}
     *
     * @param prefix
     * @return
     */
    private Map<String, Object> findVariables(String prefix) {
        Map<String, Object> map = new HashMap<>();
        Iterator<String> keyIterator = jobProps.keySet().iterator();
        while (keyIterator.hasNext()) {
            String next = keyIterator.next();
            if (next.startsWith(prefix)) {
                map.put(next.substring(prefix.length()), jobProps.get(next));
            }
        }
        return map;
    }

    /**
     * Looking for configuration through the corresponding prefix includes startup,runtime,special etc
     *
     * @param prefix
     * @return
     */
    private Map<String, Object> findConfiguration(String prefix) {
        Map<String, Object> configuration = new HashMap<>();
        Iterator<String> keyIterator = jobProps.keySet().iterator();
        while (keyIterator.hasNext()) {
            String next = keyIterator.next();
            if (next.startsWith(prefix)) {
                String confTypeAndName = next.substring(prefix.length());
                if (confTypeAndName.startsWith(LinkisJobExecutionConfiguration.CONF_STARTUP)) {
                    putConf(configuration, LinkisJobExecutionConfiguration.CONF_STARTUP, jobProps.get(next), confTypeAndName);
                } else if (confTypeAndName.startsWith(LinkisJobExecutionConfiguration.CONF_RUNTIME)) {
                    putConf(configuration, LinkisJobExecutionConfiguration.CONF_RUNTIME, jobProps.get(next), confTypeAndName);
                } else if (confTypeAndName.startsWith(LinkisJobExecutionConfiguration.CONF_SPECIAL)) {
                    putConf(configuration, LinkisJobExecutionConfiguration.CONF_SPECIAL, jobProps.get(next), confTypeAndName);
                }
            }
        }
        return configuration;
    }

    private void putConf(Map<String, Object> configuration, String key, String value, String confTypeAndName) {
        if (configuration.get(key) == null) {
            Map<String, String> startup = new HashMap<>();
            startup.put(confTypeAndName.substring(key.length() + 1), value);
            configuration.put(key, startup);
        } else {
            Map<String, String> startup = (Map<String, String>) configuration.get(key);
            startup.put(confTypeAndName.substring(key.length() + 1), value);
        }
    }


    private Map<String, List<BMLResource>> findFLowNameAndResources() {
        Map<String, List<BMLResource>> flowNameAndResources = new HashMap<>();
        Iterator<String> iterator = jobProps.keySet().iterator();
        while (iterator.hasNext()) {
            String next = iterator.next();
            if (next.endsWith("resources") && next.startsWith("flow")) {
                flowNameAndResources.put(next, LinkisJobExecutionUtils.getResourceListByJson(jobProps.get(next)));
            }
        }
        return flowNameAndResources;
    }


}
