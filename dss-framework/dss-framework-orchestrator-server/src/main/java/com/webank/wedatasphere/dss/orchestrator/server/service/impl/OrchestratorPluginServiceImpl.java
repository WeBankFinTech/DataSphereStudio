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
import com.webank.wedatasphere.dss.common.utils.DSSExceptionUtils;
import com.webank.wedatasphere.dss.orchestrator.common.entity.DSSOrchestratorInfo;
import com.webank.wedatasphere.dss.orchestrator.common.entity.DSSOrchestratorVersion;
import com.webank.wedatasphere.dss.orchestrator.common.entity.OrchestratorInfo;
import com.webank.wedatasphere.dss.orchestrator.common.protocol.RequestFrameworkConvertOrchestration;
import com.webank.wedatasphere.dss.orchestrator.common.protocol.ResponseConvertOrchestrator;
import com.webank.wedatasphere.dss.orchestrator.common.protocol.ResponseOperateOrchestrator;
import com.webank.wedatasphere.dss.orchestrator.core.DSSOrchestratorContext;
import com.webank.wedatasphere.dss.orchestrator.db.dao.OrchestratorMapper;
import com.webank.wedatasphere.dss.orchestrator.publish.ExportDSSOrchestratorPlugin;
import com.webank.wedatasphere.dss.orchestrator.publish.job.ConversionJobEntity;
import com.webank.wedatasphere.dss.orchestrator.publish.job.OrchestratorConversionJob;
import com.webank.wedatasphere.dss.orchestrator.server.service.OrchestratorPluginService;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.linkis.common.utils.Utils;
import com.webank.wedatasphere.linkis.manager.label.builder.factory.LabelBuilderFactoryContext;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;


public class OrchestratorPluginServiceImpl implements OrchestratorPluginService {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    private OrchestratorMapper orchestratorMapper;

    @Autowired
    private DSSOrchestratorContext dssOrchestratorContext;

    private ExecutorService releaseThreadPool = Utils.newCachedThreadPool(50, "Convert-Orchestration-Thread-", true);
    private Map<String, OrchestratorConversionJob> orchestratorConversionJobMap = new ConcurrentHashMap<>();
    private AtomicInteger idGenerator = new AtomicInteger();

    @Override
    public ResponseConvertOrchestrator convertOrchestration(RequestFrameworkConvertOrchestration requestConversionOrchestration) {
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
        //把orcId装车第三方的AppId
        List<Long> refAppIdList = new ArrayList<>();
        List<Long> publishedOrcIds;
        List<DSSLabel> labels = LabelBuilderFactoryContext.getLabelBuilderFactory().getLabels(requestConversionOrchestration.getLabels());
        if(requestConversionOrchestration.isConvertAllOrcs()) {
            //这个地方应该是要获取所有的已经发布过的orchestrator
            publishedOrcIds = orchestratorMapper.getAllOrcIdsByProjectId(projectId);
            if (!publishedOrcIds.contains(toPublishOrcId)){
                publishedOrcIds.add(toPublishOrcId);
            }
            for (Long orcId : publishedOrcIds) {
                DSSOrchestratorVersion dssOrchestratorVersion = orchestratorMapper.getLatestOrchestratorVersionById(orcId);
                refAppIdList.add(dssOrchestratorVersion.getAppId());
            }
        } else {
            publishedOrcIds = new ArrayList<>();
            if(requestConversionOrchestration.getOrcAppId() != null) {
                publishedOrcIds.add(toPublishOrcId);
                DSSOrchestratorVersion dssOrchestratorVersion = orchestratorMapper.getLatestOrchestratorVersionById(toPublishOrcId);
                refAppIdList.add(dssOrchestratorVersion.getAppId());
            }
            if(requestConversionOrchestration.getOrcIds() != null && !requestConversionOrchestration.getOrcIds().isEmpty()) {
                for (Long orcId : requestConversionOrchestration.getOrcIds()) {
                    DSSOrchestratorVersion dssOrchestratorVersion = orchestratorMapper.getLatestOrchestratorVersionById(orcId);
                    publishedOrcIds.add(orcId);
                    refAppIdList.add(dssOrchestratorVersion.getAppId());
                }
            }
        }
        OrchestratorConversionJob job = new OrchestratorConversionJob();
        job.setId(generateId());
        ConversionJobEntity entity = new ConversionJobEntity();
        entity.setResponse(ResponseOperateOrchestrator.inited());
        entity.setCreateTime(new Date());
        entity.setUserName(requestConversionOrchestration.getUserName());
        entity.setOrcIdList(publishedOrcIds);
        entity.setRefAppIdList(refAppIdList);
        entity.setLabels(labels);
        entity.setWorkspace((Workspace) requestConversionOrchestration.getWorkspace());
        DSSProject dssProject = new DSSProject();
        dssProject.setId(projectId);
        entity.setProject(dssProject);
        job.setConversionJobEntity(entity);
        job.setConversionDSSOrchestratorPlugins(dssOrchestratorContext.getOrchestratorPlugins());
        job.afterConversion(response -> this.updateDBAfterConversion(response, job, requestConversionOrchestration));
        //submit it
        releaseThreadPool.submit(job);
        orchestratorConversionJobMap.put(job.getId(), job);
        return new ResponseConvertOrchestrator(job.getId(), entity.getResponse());
    }

    @Override
    public ResponseConvertOrchestrator getConvertOrchestrationStatus(String id) {
        OrchestratorConversionJob job = orchestratorConversionJobMap.get(id);
        if(job.getConversionJobEntity().getResponse().isCompleted()) {
            orchestratorConversionJobMap.remove(id);
        }
        return new ResponseConvertOrchestrator(job.getId(), job.getConversionJobEntity().getResponse());
    }

    private String generateId() {
        return String.valueOf(idGenerator.getAndIncrement());
    }

    @Transactional(rollbackFor = Exception.class)
    private void updateDBAfterConversion(ResponseOperateOrchestrator response,
        OrchestratorConversionJob job,
        RequestFrameworkConvertOrchestration requestConversionOrchestration) {
        LOGGER.info("{} completed with status {}.", job.getId(), response.getJobStatus());
        if(response.isSucceed()) {
            //1. 进行导出,用于升级版本,目的是为了复用原来的代码
            List<Long> orcIdList = job.getConversionJobEntity().getOrcIdList();
            orcIdList.forEach(DSSExceptionUtils.handling(orcId -> {
                DSSOrchestratorInfo dssOrchestratorInfo = orchestratorMapper.getOrchestrator(orcId);
                dssOrchestratorContext.getDSSOrchestratorPlugin(ExportDSSOrchestratorPlugin.class)
                    .orchestratorVersionIncrease(orcId, job.getConversionJobEntity().getUserName(), requestConversionOrchestration.getComment(),
                        requestConversionOrchestration.getWorkspace().getWorkspaceName(), dssOrchestratorInfo,
                        job.getConversionJobEntity().getProject().getName(), job.getConversionJobEntity().getLabels());
            }));
            //2. 做一个标记表示已经发布过了
            orcIdList.forEach(orchestratorMapper::setPublished);
        }
    }
}
