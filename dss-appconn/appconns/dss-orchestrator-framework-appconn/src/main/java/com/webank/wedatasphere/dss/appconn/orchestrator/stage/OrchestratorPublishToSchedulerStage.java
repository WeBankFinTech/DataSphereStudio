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

package com.webank.wedatasphere.dss.appconn.orchestrator.stage;

import com.webank.wedatasphere.dss.appconn.orchestrator.operation.OrchestratorFrameworkUpdateOperation;
import com.webank.wedatasphere.dss.appconn.orchestrator.ref.DefaultOrchestratorPublishResponseRef;
import com.webank.wedatasphere.dss.common.entity.DSSLabel;
import com.webank.wedatasphere.dss.common.protocol.JobStatus;
import com.webank.wedatasphere.dss.common.utils.DSSCommonUtils;
import com.webank.wedatasphere.dss.common.utils.DSSExceptionUtils;
import com.webank.wedatasphere.dss.orchestrator.common.protocol.ResponsePublishOrchestrator;
import com.webank.wedatasphere.dss.orchestrator.core.protocol.RequestPublishOrchestrator;
import com.webank.wedatasphere.dss.orchestrator.core.ref.OrchestratorPublishResponseRef;
import com.webank.wedatasphere.dss.standard.app.development.DevelopmentService;
import com.webank.wedatasphere.dss.standard.app.development.publish.scheduler.*;
import com.webank.wedatasphere.dss.standard.app.development.stage.GetRefStage;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.linkis.common.exception.WarnException;
import com.webank.wedatasphere.linkis.rpc.Sender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author allenlliu
 * @date 2020/12/15 20:16
 */
public class OrchestratorPublishToSchedulerStage extends AbstractPublishToSchedulerStage {

    private static final String ORCHESTRATOR_NAME = "DSS-Framework-Orchestrator-Server";

    private static final Logger LOGGER = LoggerFactory.getLogger(OrchestratorFrameworkUpdateOperation.class);

    private DevelopmentService developmentService;

    private RefScheduleOperation refScheduleOperation;

    @Override
    public void setSchedulerRefOperation(RefScheduleOperation refScheduleOperation) {
         this.refScheduleOperation = refScheduleOperation;
    }


    @Override
    protected GetRefStage createGetRefStage() {
        return null;
    }


    private Sender getAccurateSender(List<DSSLabel> dssLabels){
        for (DSSLabel dssLabel : dssLabels){
            if ("DEV".equalsIgnoreCase(dssLabel.getLabel())){
                return Sender.getSender(ORCHESTRATOR_NAME + "-Dev");
            }
        }
        return Sender.getSender(ORCHESTRATOR_NAME + "-Prod");
    }


    @Override
    public ResponseRef publishToScheduler(PublishToSchedulerRef ref) throws ExternalOperationFailedException {
        if (null == ref){
            return null;
        }
        if (ref instanceof ProjectPublishToSchedulerRef){
            LOGGER.info("Begin to ask to publish orchestrator, requestRef is {}", DSSCommonUtils.COMMON_GSON.toJson(ref));
            ProjectPublishToSchedulerRef realRef = (ProjectPublishToSchedulerRef)ref;
            RequestPublishOrchestrator publishRequest = new RequestPublishOrchestrator(realRef.getUserName(),
                    realRef.getProject().getName(),
                    realRef.getProject().getId(),
                    realRef.getOrcIds(),
                    realRef.getPublishType(),
                    realRef.getWorkspace(),
                    realRef.getLabels()
                    );
            ResponsePublishOrchestrator publishResponse = null;
            try{
                Sender sender = getAccurateSender(ref.getLabels());
                Object object =  sender.ask(publishRequest);
                if (object instanceof Throwable){
                    throw (Throwable)object;
                }
                publishResponse = (ResponsePublishOrchestrator)object;
            }catch(Throwable e){
                if(e instanceof WarnException){
                    WarnException warnException = (WarnException)e;
                    DSSExceptionUtils.dealErrorException(60014, warnException.getDesc(), e,
                            ExternalOperationFailedException.class);
                }else {
                    DSSExceptionUtils.dealErrorException(60015, "publish orchestrator failed", e,
                            ExternalOperationFailedException.class);
                }
            }
            LOGGER.info("End to ask to publish orchestrator, responseRef is {}", DSSCommonUtils.COMMON_GSON.toJson(publishResponse));
            OrchestratorPublishResponseRef publishResponseRef = new DefaultOrchestratorPublishResponseRef();
            if(publishResponse.getJobStatus().equals(JobStatus.Success)) {
                publishResponseRef.setPublishResult(true);
            }else {
                publishResponseRef.setPublishResult(false);
            }
            return publishResponseRef;
        }else{
            LOGGER.warn("requestRef is not a type of ProjectPublishToSchedulerRef, it is type of {}",ref.getClass().getName());
            //todo 为不是是适配的requestRef 那么要从params中获取的想要的内容
            return null;
        }
    }
}
