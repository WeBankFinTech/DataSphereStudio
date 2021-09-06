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

package com.webank.wedatasphere.dss.linkis.node.execution.service.impl;

import com.google.gson.Gson;
import com.webank.wedatasphere.dss.linkis.node.execution.conf.LinkisJobExecutionConfiguration;
import com.webank.wedatasphere.dss.linkis.node.execution.exception.LinkisJobExecutionErrorException;
import com.webank.wedatasphere.dss.linkis.node.execution.job.Job;
import com.webank.wedatasphere.dss.linkis.node.execution.job.LinkisJob;
import com.webank.wedatasphere.dss.linkis.node.execution.service.BuildJobAction;
import com.webank.wedatasphere.linkis.manager.label.constant.LabelKeyConstant;
import com.webank.wedatasphere.linkis.manager.label.entity.engine.EngineTypeLabel;
import com.webank.wedatasphere.linkis.manager.label.utils.EngineTypeLabelCreator;
import com.webank.wedatasphere.linkis.protocol.utils.TaskUtils;
import com.webank.wedatasphere.linkis.ujes.client.request.JobExecuteAction;
import com.webank.wedatasphere.linkis.ujes.client.request.JobSubmitAction;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.webank.wedatasphere.dss.linkis.node.execution.conf.LinkisJobExecutionConfiguration.LINKIS_JOB_CREATOR;


public class BuildJobActionImpl implements BuildJobAction {

    private Logger logger = LoggerFactory.getLogger(BuildJobActionImpl.class);
    private static BuildJobAction buildJobAction = new BuildJobActionImpl();

    private BuildJobActionImpl() {

    }

    public static BuildJobAction getbuildJobAction() {
        return buildJobAction;
    }

    private Map<String, Object> prepareYarnLabel(Job job) {
        Map<String, Object> labels = new HashMap<>();

        Object labelStr = job.getRuntimeParams().getOrDefault(LinkisJobExecutionConfiguration.DSS_LABELS_KEY, null);

        if (StringUtils.isNotBlank((String) labelStr)) {
            labels.put(LinkisJobExecutionConfiguration.DSS_LABELS_KEY, labelStr);
            logger.info("Set Yarn cluster label is: " + labels.toString());
        } else {
            labels.put(LinkisJobExecutionConfiguration.DSS_LABELS_KEY, "DEV-DEV");
            logger.warn("Job Label is null. Then set default value");
        }
        return labels;
    }

    private String parseExecutionCode(Job job) {
        String code = job.getCode();
        if (StringUtils.isEmpty(code) || code.equalsIgnoreCase("null")) {
            Gson gson = new Gson();
            code = gson.toJson(job.getParams());
            logger.info("The executable code for the job is {}", code);
        }
        return code;
    }


    @Override
    public JobExecuteAction getJobAction(Job job) throws LinkisJobExecutionErrorException {

        enrichParams(job);

        Map<String, Object> labels = prepareYarnLabel(job);

        TaskUtils.addLabelsMap(job.getParams(), labels);

        String code = parseExecutionCode(job);

        JobExecuteAction.Builder builder = JobExecuteAction.builder().setCreator(LINKIS_JOB_CREATOR.getValue(job.getJobProps()))
                .addExecuteCode(code)
                .setEngineTypeStr(parseAppConnEngineType(job.getEngineType(), job))
                .setRunTypeStr(parseRunType(job.getEngineType(), job.getRunType(), job))
                .setUser(job.getUser())
                .setParams(job.getParams())
                .setRuntimeParams(job.getRuntimeParams());
        if (job instanceof LinkisJob) {
            Map<String, Object> source = new HashMap<>();
            source.putAll(((LinkisJob) job).getSource());
            builder = builder.setSource(source);
        }
        return builder.build();
    }

    @Override
    public JobSubmitAction getSubmitAction(Job job) throws LinkisJobExecutionErrorException {
        enrichParams(job);

        Map<String, Object> labels = prepareYarnLabel(job);

        TaskUtils.addLabelsMap(job.getParams(), labels);

        String code = parseExecutionCode(job);

        EngineTypeLabel engineTypeLabel = EngineTypeLabelCreator.createEngineTypeLabel(parseAppConnEngineType(job.getEngineType(), job));

        labels.put(LabelKeyConstant.ENGINE_TYPE_KEY, engineTypeLabel.getStringValue());
        labels.put(LabelKeyConstant.USER_CREATOR_TYPE_KEY, job.getUser() + "-" + LINKIS_JOB_CREATOR.getValue());
        labels.put(LabelKeyConstant.CODE_TYPE_KEY, parseRunType(job.getEngineType(), job.getRunType(), job));


        //是否复用引擎，不复用就为空
        if(!isReuseEngine(job.getParams())){
            labels.put("executeOnce", "");
        }
        JobSubmitAction.Builder builder = JobSubmitAction.builder().setUser(LINKIS_JOB_CREATOR.getValue(job.getJobProps()))
                .addExecuteCode(code)
                .setUser(job.getUser())
                .addExecuteUser(job.getUser())
                .setParams(job.getParams())
                .setLabels(labels)
                .setRuntimeParams(job.getRuntimeParams());
        if (job instanceof LinkisJob) {
            Map<String, Object> source = new HashMap<>();
            source.putAll(((LinkisJob) job).getSource());
            builder = builder.setSource(source);
        }
        return builder.build();
    }

    /**
     * 是否复用引擎，复用返回：true，不复用：false
     * @param params
     * @return
     */
    public boolean isReuseEngine(Map<String, Object> params) {
        if (params.get("configuration") != null) {
            Map<String, Object> configurationMap = (Map<String, Object>) params.get("configuration");
            if (configurationMap.get("startup") != null) {
                Map<String, Object> startupMap = (Map<String, Object>) configurationMap.get("startup");
                if (startupMap.get("ReuseEngine") != null) {
                    String reuseEngine = (String) startupMap.get("ReuseEngine");
                    if (StringUtils.isNotBlank(reuseEngine) && "false".equalsIgnoreCase(reuseEngine.trim())) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    //TODO
    private String parseAppConnEngineType(String engineType, Job job) {
        Map<String, String> props = job.getJobProps();
        if (LinkisJobExecutionConfiguration.isLinkis1_X(props) && engineType.equalsIgnoreCase("appjoint")) {
            return LinkisJobExecutionConfiguration.APPCONN;
        }
        return engineType;
    }

    private String parseRunType(String engineType, String runType, Job job) {
        Map<String, String> props = job.getJobProps();
        if (LinkisJobExecutionConfiguration.isLinkis1_X(props) && engineType.equalsIgnoreCase("appjoint")) {
            return LinkisJobExecutionConfiguration.APPCONN;
        } else if (engineType.toLowerCase().contains("shell")) {
            return "shell";
        }

        return runType;
    }

    private void enrichParams(Job job) {
        job.getRuntimeParams().put("nodeType", job.getRunType());
    }
}
