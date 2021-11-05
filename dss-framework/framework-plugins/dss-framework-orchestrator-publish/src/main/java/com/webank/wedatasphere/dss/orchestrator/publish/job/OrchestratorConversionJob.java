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

package com.webank.wedatasphere.dss.orchestrator.publish.job;

import com.webank.wedatasphere.dss.common.entity.project.DSSProject;
import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.common.protocol.project.ProjectInfoRequest;
import com.webank.wedatasphere.dss.orchestrator.common.protocol.ResponseOperateOrchestrator;
import com.webank.wedatasphere.dss.orchestrator.core.plugin.DSSOrchestratorPlugin;
import com.webank.wedatasphere.dss.orchestrator.publish.ConversionDSSOrchestratorPlugin;
import com.webank.wedatasphere.dss.sender.service.DSSSenderServiceFactory;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.linkis.common.utils.ByteTimeUtils;
import java.util.List;
import java.util.function.Consumer;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public final class OrchestratorConversionJob implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrchestratorConversionJob.class);

    private String id;

    private ConversionJobEntity conversionJobEntity;
    private List<DSSOrchestratorPlugin> conversionDSSOrchestratorPlugins;
    private Consumer<ResponseOperateOrchestrator> consumer;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setConversionJobEntity(ConversionJobEntity conversionJobEntity) {
        this.conversionJobEntity = conversionJobEntity;
    }

    public ConversionJobEntity getConversionJobEntity() {
        return conversionJobEntity;
    }

    public void setConversionDSSOrchestratorPlugins(List<DSSOrchestratorPlugin> conversionDSSOrchestratorPlugins) {
        this.conversionDSSOrchestratorPlugins = conversionDSSOrchestratorPlugins;
    }

    public void afterConversion(Consumer<ResponseOperateOrchestrator> consumer) {
        this.consumer = consumer;
    }

    @Override
    public void run() {
        //1.从编排中心导出一次工作流,进行一次版本升级
        //2.进行发布到schedulis等调度系统
        LOGGER.info("Begin to convert project {} for user {} to scheduler, the orcIdList is {}.",
            conversionJobEntity.getProject().getId(), conversionJobEntity.getUserName(), conversionJobEntity.getOrcIdList());
        conversionJobEntity.setResponse(ResponseOperateOrchestrator.running());
        ConversionDSSOrchestratorPlugin conversionDSSOrchestratorPlugin = null;
        for (DSSOrchestratorPlugin plugin: conversionDSSOrchestratorPlugins) {
            if(plugin instanceof ConversionDSSOrchestratorPlugin) {
                conversionDSSOrchestratorPlugin = (ConversionDSSOrchestratorPlugin) plugin;
            }
        }
        ProjectInfoRequest projectInfoRequest = new ProjectInfoRequest();
        projectInfoRequest.setProjectId(conversionJobEntity.getProject().getId());
        try{
            DSSProject project = (DSSProject) DSSSenderServiceFactory.getOrCreateServiceInstance().getProjectServerSender()
                .ask(projectInfoRequest);
            conversionJobEntity.setProject(project);
            Workspace workspace = conversionJobEntity.getWorkspace();
            ResponseOperateOrchestrator response = conversionDSSOrchestratorPlugin.convert(conversionJobEntity.getUserName(), project, workspace,
                conversionJobEntity.getRefAppIdList(), conversionJobEntity.getLabels());
            if(response.isFailed()) {
                String msg = response.getMessage() == null ? "Unknown reason, please ask admin for help!" : response.getMessage();
                throw new DSSErrorException(50000, msg);
            }
            //3.如果都没有报错，那么默认任务应该是成功的,那么则将所有的状态进行置为完成
            consumer.accept(response);
            conversionJobEntity.setResponse(response);
        } catch (final Exception t){
            LOGGER.error("Convert for project {} failed.", conversionJobEntity.getProject(), t);
            ResponseOperateOrchestrator response = ResponseOperateOrchestrator.failed(ExceptionUtils.getRootCauseMessage(t));
            conversionJobEntity.setResponse(response);
            consumer.accept(response);
        }
        LOGGER.info("convert project {} for user {} to scheduler {}, costs {}.", conversionJobEntity.getResponse().getJobStatus(), conversionJobEntity.getProject().getId(),
            conversionJobEntity.getUserName(), ByteTimeUtils.msDurationToString(conversionJobEntity.getUpdateTime().getTime() - conversionJobEntity.getCreateTime().getTime()));
    }
}
