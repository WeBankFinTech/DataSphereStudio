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

package com.webank.wedatasphere.dss.orchestrator.server.service.impl;

import com.webank.wedatasphere.dss.common.entity.project.DSSProject;
import com.webank.wedatasphere.dss.common.label.DSSLabel;
import com.webank.wedatasphere.dss.common.label.DSSLabelUtil;
import com.webank.wedatasphere.dss.common.utils.DSSCommonUtils;
import com.webank.wedatasphere.dss.common.utils.DSSExceptionUtils;
import com.webank.wedatasphere.dss.orchestrator.common.entity.DSSOrchestratorInfo;
import com.webank.wedatasphere.dss.orchestrator.common.entity.DSSOrchestratorRefOrchestration;
import com.webank.wedatasphere.dss.orchestrator.common.entity.DSSOrchestratorVersion;
import com.webank.wedatasphere.dss.orchestrator.common.entity.OrchestratorInfo;
import com.webank.wedatasphere.dss.orchestrator.common.protocol.RequestFrameworkConvertOrchestration;
import com.webank.wedatasphere.dss.orchestrator.common.protocol.ResponseConvertOrchestrator;
import com.webank.wedatasphere.dss.orchestrator.common.protocol.ResponseOperateOrchestrator;
import com.webank.wedatasphere.dss.orchestrator.core.DSSOrchestratorContext;
import com.webank.wedatasphere.dss.orchestrator.db.dao.OrchestratorJobMapper;
import com.webank.wedatasphere.dss.orchestrator.db.dao.OrchestratorMapper;
import com.webank.wedatasphere.dss.orchestrator.publish.ExportDSSOrchestratorPlugin;
import com.webank.wedatasphere.dss.orchestrator.publish.job.ConversionJobEntity;
import com.webank.wedatasphere.dss.orchestrator.publish.job.OrchestratorConversionJob;
import com.webank.wedatasphere.dss.orchestrator.server.constant.DSSOrchestratorConstant;
import com.webank.wedatasphere.dss.orchestrator.server.service.OrchestratorPluginService;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import org.apache.linkis.common.utils.Utils;
import org.apache.linkis.manager.label.builder.factory.LabelBuilderFactoryContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;


public class OrchestratorPluginServiceImpl implements OrchestratorPluginService {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    private OrchestratorMapper orchestratorMapper;

    @Autowired
    private OrchestratorJobMapper orchestratorJobMapper;

    @Autowired
    private DSSOrchestratorContext dssOrchestratorContext;

    private ExecutorService releaseThreadPool = Utils.newCachedThreadPool(50, "Convert-Orchestration-Thread-", true);
    private AtomicInteger idGenerator = new AtomicInteger();

    @Override
    public ResponseConvertOrchestrator convertOrchestration(RequestFrameworkConvertOrchestration requestConversionOrchestration) {
        LOGGER.info("conversion request is called, request is {}.", DSSCommonUtils.COMMON_GSON.toJson(requestConversionOrchestration));
        Long toPublishOrcId;
        if(requestConversionOrchestration.getOrcAppId() != null) {
            OrchestratorInfo orchestratorInfo = orchestratorMapper.getOrcInfoByAppId(requestConversionOrchestration.getOrcAppId());
            toPublishOrcId = orchestratorInfo.getOrchestratorId();
        } else if(requestConversionOrchestration.getOrcIds() != null && !requestConversionOrchestration.getOrcIds().isEmpty()) {
            toPublishOrcId = requestConversionOrchestration.getOrcIds().get(0);
        } else {
            return new ResponseConvertOrchestrator("-1", ResponseOperateOrchestrator.failed("Both orcAppId and orcIds are not exists."));
        }
        DSSOrchestratorInfo dssOrchestratorInfo = orchestratorMapper.getOrchestrator(toPublishOrcId);
        long projectId = dssOrchestratorInfo.getProjectId();
        //这里的 key 为 DSS 具体编排（如 DSS 工作流）的 id；这里的 value 为 DSS 编排所对应的第三方调度系统的工作流 ID
        //请注意：由于对接的 SchedulerAppConn 调度系统有可能没有实现 OrchestrationService，
        //所以可能存在 DSS 在创建 DSS 编排时，无法同步去 SchedulerAppConn 创建工作流的情况，从而导致这个 Map 的所有 value 都为 null。
        Map<Long, Long> orchestrationIdMap = new HashMap<>();
        List<Long> publishedOrcIds;
        List<DSSLabel> labels = LabelBuilderFactoryContext.getLabelBuilderFactory().getLabels(requestConversionOrchestration.getLabels());
        if(requestConversionOrchestration.isConvertAllOrcs()) {
            //这个地方应该是要获取所有的已经发布过的orchestrator
            publishedOrcIds = orchestratorMapper.getAllOrcIdsByProjectId(projectId);
            for (Long orcId : publishedOrcIds) {
                int validFlag = 1;
                if (orcId.longValue() == toPublishOrcId.longValue() && !DSSLabelUtil.isDevEnv(labels)) {
                    validFlag = 0;
                }
                DSSOrchestratorVersion dssOrchestratorVersion = orchestratorMapper.getLatestOrchestratorVersionByIdAndValidFlag(orcId, validFlag);
                if (dssOrchestratorVersion == null) {
                    continue;
                }
                DSSOrchestratorRefOrchestration dssOrchestratorRefOrchestration = orchestratorMapper.getRefOrchestrationId(orcId);
                if(dssOrchestratorRefOrchestration != null) {
                    orchestrationIdMap.put(dssOrchestratorVersion.getAppId(), dssOrchestratorRefOrchestration.getRefOrchestrationId());
                } else {
                    orchestrationIdMap.put(dssOrchestratorVersion.getAppId(), null);
                }
            }
        } else {
            publishedOrcIds = new ArrayList<>();
            if(requestConversionOrchestration.getOrcAppId() != null) {
                publishedOrcIds.add(toPublishOrcId);
                DSSOrchestratorVersion dssOrchestratorVersion = orchestratorMapper.getLatestOrchestratorVersionByIdAndValidFlag(toPublishOrcId,1);
                if (dssOrchestratorVersion != null) {
                    DSSOrchestratorRefOrchestration dssOrchestratorRefOrchestration = orchestratorMapper.getRefOrchestrationId(toPublishOrcId);
                    if(dssOrchestratorRefOrchestration != null) {
                        orchestrationIdMap.put(dssOrchestratorVersion.getAppId(), dssOrchestratorRefOrchestration.getRefOrchestrationId());
                    } else {
                        orchestrationIdMap.put(dssOrchestratorVersion.getAppId(), null);
                    }
                }
            }
            if(requestConversionOrchestration.getOrcIds() != null && !requestConversionOrchestration.getOrcIds().isEmpty()) {
                for (Long orcId : requestConversionOrchestration.getOrcIds()) {
                    DSSOrchestratorVersion dssOrchestratorVersion = orchestratorMapper.getLatestOrchestratorVersionByIdAndValidFlag(orcId,1);
                    if (dssOrchestratorVersion == null) {
                        continue;
                    }
                    publishedOrcIds.add(orcId);
                    DSSOrchestratorRefOrchestration dssOrchestratorRefOrchestration = orchestratorMapper.getRefOrchestrationId(orcId);
                    if(dssOrchestratorRefOrchestration != null) {
                        orchestrationIdMap.put(dssOrchestratorVersion.getAppId(), dssOrchestratorRefOrchestration.getRefOrchestrationId());
                    } else {
                        orchestrationIdMap.put(dssOrchestratorVersion.getAppId(), null);
                    }
                }
            }
        }
        if(orchestrationIdMap.isEmpty()) {
            LOGGER.info("the project {} has no suitable workflow, the publish by user {} is ignored.", projectId,
                    requestConversionOrchestration.getUserName());
            return new ResponseConvertOrchestrator("no-necessary-id", ResponseOperateOrchestrator.failed("No suitable workflow(s) found, publish is ignored."));
        }
        OrchestratorConversionJob job = new OrchestratorConversionJob();
        job.setId(generateId());
        LOGGER.info("user {} try to submit a conversion job {}, the orchestrationIdMap is {}, the orcIdList is {}.", requestConversionOrchestration.getUserName(),
                job.getId(), orchestrationIdMap, publishedOrcIds);
        ConversionJobEntity entity = new ConversionJobEntity();
        entity.setResponse(ResponseOperateOrchestrator.inited());
        entity.setCreateTime(new Date());
        entity.setUserName(requestConversionOrchestration.getUserName());
        entity.setOrchestrationIdMap(orchestrationIdMap);
        entity.setOrcIdList(publishedOrcIds);
        entity.setLabels(labels);
        entity.setWorkspace((Workspace) requestConversionOrchestration.getWorkspace());
        DSSProject dssProject = new DSSProject();
        dssProject.setId(projectId);
        entity.setProject(dssProject);
        job.setConversionJobEntity(entity);
        job.setConversionDSSOrchestratorPlugins(dssOrchestratorContext.getOrchestratorPlugins());
        job.afterConversion(response -> this.updateDBAfterConversion(toPublishOrcId, response, job, requestConversionOrchestration));
        //submit it
        releaseThreadPool.submit(job);
        DSSOrchestratorConstant.orchestratorConversionJobMap.put(job.getId(), job);
        // todo insert publishJob
        return new ResponseConvertOrchestrator(job.getId(), entity.getResponse());
    }

    @Override
    public ResponseConvertOrchestrator getConvertOrchestrationStatus(String id) {
        OrchestratorConversionJob job = DSSOrchestratorConstant.orchestratorConversionJobMap.get(id);
        // todo 找不到从db加载
        if(job.getConversionJobEntity().getResponse().isCompleted()) {
            DSSOrchestratorConstant.orchestratorConversionJobMap.remove(job.getId());
        }
        return new ResponseConvertOrchestrator(job.getId(), job.getConversionJobEntity().getResponse());
    }

    private String generateId() {
        return String.valueOf(idGenerator.getAndIncrement());
    }

    @Transactional(rollbackFor = Exception.class)
    private void updateDBAfterConversion(Long toPublishOrcId,ResponseOperateOrchestrator response,
                                         OrchestratorConversionJob job,
                                         RequestFrameworkConvertOrchestration requestConversionOrchestration) {
        LOGGER.info("{} completed with status {}.", job.getId(), response.getJobStatus());
        if(response.isSucceed()) {
            //1. 进行导出,用于升级版本,目的是为了复用原来的代码
            List<Long> orcIdList = job.getConversionJobEntity().getOrcIdList();
            // 开发环境才新增版本号
            if(DSSLabelUtil.isDevEnv(job.getConversionJobEntity().getLabels())){
                orcIdList.forEach(DSSExceptionUtils.handling(orcId -> {
                    DSSOrchestratorInfo dssOrchestratorInfo = orchestratorMapper.getOrchestrator(orcId);
                    dssOrchestratorContext.getDSSOrchestratorPlugin(ExportDSSOrchestratorPlugin.class)
                            .orchestratorVersionIncrease(orcId, job.getConversionJobEntity().getUserName(), requestConversionOrchestration.getComment(),
                                    (Workspace) requestConversionOrchestration.getWorkspace(), dssOrchestratorInfo,
                                    job.getConversionJobEntity().getProject().getName(), job.getConversionJobEntity().getLabels());
                }));
            }
            //2. 做一个标记表示已经发布过了
            orcIdList.forEach(orchestratorMapper::setPublished);
            //3.更新当前的提交的发布编排模式版本的common
            updateCurrentPublishOrchestratorCommon(toPublishOrcId,requestConversionOrchestration.getComment(),job.getConversionJobEntity().getLabels());
        }
    }

    public void updateCurrentPublishOrchestratorCommon(Long orcId,String realComment,List<DSSLabel> dssLabels) {
        //DEV环境获取获取最新有效的版本即可
        int validFlag = 1;
        if (!DSSLabelUtil.isDevEnv(dssLabels)) {
            //PROD获取当前编排模式最新版本的ID
            validFlag = 0;
        }
        DSSOrchestratorVersion version = orchestratorMapper.getLatestOrchestratorVersionByIdAndValidFlag(orcId,validFlag);
        DSSOrchestratorVersion updateCommentVersion = new DSSOrchestratorVersion();
        updateCommentVersion.setId(version.getId());
        updateCommentVersion.setComment(realComment);
        updateCommentVersion.setUpdateTime(new Date());
        updateCommentVersion.setValidFlag(1);
        orchestratorMapper.updateOrchestratorVersion(updateCommentVersion);
    }
}
