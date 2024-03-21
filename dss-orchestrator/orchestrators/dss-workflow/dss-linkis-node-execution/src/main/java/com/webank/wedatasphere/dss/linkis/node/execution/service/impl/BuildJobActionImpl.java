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
import com.webank.wedatasphere.dss.linkis.node.execution.utils.LinkisJobExecutionUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.linkis.manager.label.constant.LabelKeyConstant;
import org.apache.linkis.manager.label.entity.engine.EngineTypeLabel;
import org.apache.linkis.manager.label.utils.EngineTypeLabelCreator;
import org.apache.linkis.protocol.constants.TaskConstant;
import org.apache.linkis.protocol.utils.TaskUtils;
import org.apache.linkis.ujes.client.request.JobExecuteAction;
import org.apache.linkis.ujes.client.request.JobSubmitAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.webank.wedatasphere.dss.linkis.node.execution.conf.LinkisJobExecutionConfiguration.*;


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
        logger.info("The parseExecutionCode0X code for the job is {}", code);
        if (StringUtils.isEmpty(code) || code.equalsIgnoreCase("null")) {
            code = LinkisJobExecutionUtils.gson.toJson(job.getParams());
            logger.info("The executable code for the job is {}", code);
        }
        return code;
    }


    private String parseExecutionCodeFor1X(Job job) {
        String code = job.getCode();
        logger.info("The parseExecutionCodeFor1X code for the job is {}", code);
        //for appconn node  in subflow  contains embeddedFlowId
        if (StringUtils.isEmpty(code) || code.equalsIgnoreCase("null") || code.contains(EMBEDDED_FLOW_ID.getValue())) {
            code = LinkisJobExecutionUtils.gson.toJson(job.getParams());
            logger.info("The executable code for the job is {}", code);
        }
        return code;
    }


    @Override
    public JobExecuteAction getJobAction(Job job) throws LinkisJobExecutionErrorException {

        enrichParams(job);

//        Map<String, Object> labels = prepareYarnLabel(job);
//
//        TaskUtils.addLabelsMap(job.getParams(), labels);

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

        String code = parseExecutionCodeFor1X(job);

        EngineTypeLabel engineTypeLabel = EngineTypeLabelCreator.createEngineTypeLabel(parseAppConnEngineType(job.getEngineType(), job));

        labels.put(LabelKeyConstant.ENGINE_TYPE_KEY, engineTypeLabel.getStringValue());
        labels.put(LabelKeyConstant.USER_CREATOR_TYPE_KEY, job.getUser() + "-" + LINKIS_JOB_CREATOR_1_X.getValue(job.getJobProps()));
        labels.put(LabelKeyConstant.CODE_TYPE_KEY, parseRunType(job.getEngineType(), job.getRunType(), job));


        //是否复用引擎，不复用就为空
        if(!isReuseEngine(job.getParams())){
            labels.put("executeOnce", "");
        }
        Map<String, Object> paramMapCopy = (HashMap<String, Object>) SerializationUtils.clone(new HashMap<String, Object>(job.getParams()));
        replaceSparkConfParams(paramMapCopy);

        JobSubmitAction.Builder builder = JobSubmitAction.builder()
                .addExecuteCode(code)
                .addExecuteUser(job.getUser())
                .setParams(paramMapCopy)
                .setLabels(labels)
                .setRuntimeParams(job.getRuntimeParams());
        if (job instanceof LinkisJob) {
            LinkisJob linkisJob = (LinkisJob) job;
            builder = builder.setUser(linkisJob.getSubmitUser());
            Map<String, Object> source = new HashMap<>();
            source.putAll(linkisJob.getSource());
            builder = builder.setSource(source);
        }else{
            builder = builder.setUser(job.getUser());
        }
        // 将execute接口带来的额外variable参数，带进来  todo check
        Map<String, Object> propMap = new HashMap<>();
        propMap.putAll(job.getJobProps());
        TaskUtils.addVariableMap(paramMapCopy, TaskUtils.getVariableMap(propMap));
        builder.setParams(paramMapCopy);
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

    /**
     * spark自定义参数配置输入，例如spark.sql.shuffle.partitions=10。多个参数使用分号分隔。
     *
     * 如果节点指定了参数模板，则需要把节点内与模板相同的参数取消掉，保证模板优先级高于节点参数
     *
     * @param paramMapCopy
     * @throws LinkisJobExecutionErrorException
     */
    private void replaceSparkConfParams(Map<String, Object> paramMapCopy) throws LinkisJobExecutionErrorException {
        Map<String, Object> startupMap = TaskUtils.getStartupMap(paramMapCopy);
        logger.info("try process keys in template");
        //如果节点指定了参数模板，则需要把节点内与模板相同的参数取消掉，保证模板优先级高于节点参数
        if (startupMap.containsKey("ec.conf.templateId")) {
            logger.info("remove keys in template");
            logger.info("before remove startup map:{}",startupMap.keySet());
            startupMap.remove("spark.driver.memory");
            startupMap.remove("spark.executor.memory");
            startupMap.remove("spark.executor.cores");
            startupMap.remove("spark.executor.instances");
            startupMap.remove("wds.linkis.engineconn.java.driver.memory");
            startupMap.remove("spark.conf");
            logger.info("after remove startup map:{}",startupMap.keySet());
        }
        Map<String, Object> configurationMap = TaskUtils.getMap(paramMapCopy, TaskConstant.PARAMS_CONFIGURATION);
        configurationMap.put(TaskConstant.PARAMS_CONFIGURATION_STARTUP, startupMap);
        paramMapCopy.put(TaskConstant.PARAMS_CONFIGURATION, configurationMap);
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
