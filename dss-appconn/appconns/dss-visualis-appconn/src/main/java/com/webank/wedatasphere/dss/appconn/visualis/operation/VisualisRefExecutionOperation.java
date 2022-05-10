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

package com.webank.wedatasphere.dss.appconn.visualis.operation;

import com.webank.wedatasphere.dss.appconn.visualis.VisualisAppConn;
import com.webank.wedatasphere.dss.appconn.visualis.constant.VisualisConstant;
import com.webank.wedatasphere.dss.appconn.visualis.operation.impl.ViewOptStrategy;
import com.webank.wedatasphere.dss.appconn.visualis.operation.impl.VisualisRefExecutionAction;
import com.webank.wedatasphere.dss.appconn.visualis.utils.URLUtils;
import com.webank.wedatasphere.dss.appconn.visualis.utils.VisualisCommonUtil;
import com.webank.wedatasphere.dss.common.label.EnvDSSLabel;
import com.webank.wedatasphere.dss.standard.app.development.listener.common.RefExecutionAction;
import com.webank.wedatasphere.dss.standard.app.development.listener.common.RefExecutionState;
import com.webank.wedatasphere.dss.standard.app.development.listener.core.LongTermRefExecutionOperation;
import com.webank.wedatasphere.dss.standard.app.development.listener.ref.ExecutionResponseRef;
import com.webank.wedatasphere.dss.standard.app.development.listener.ref.RefExecutionRequestRef;
import com.webank.wedatasphere.dss.standard.app.sso.origin.request.action.DSSGetAction;
import com.webank.wedatasphere.dss.standard.common.entity.ref.InternalResponseRef;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;

public class VisualisRefExecutionOperation
        extends LongTermRefExecutionOperation<RefExecutionRequestRef.RefExecutionProjectWithContextRequestRef> {

    @Override
    protected RefExecutionAction submit(RefExecutionRequestRef.RefExecutionProjectWithContextRequestRef requestRef) throws ExternalOperationFailedException {
        String nodeType = requestRef.getType().toLowerCase();
        String nodeName = VisualisConstant.NODE_NAME_PREFIX + nodeType;
        OperationStrategy strategy = OperationStrategyFactory.getInstance().getOperationStrategy(service.getAppInstance(), nodeName);
        if (isSupportAsyncExecution(requestRef, strategy)) {
            return executeAsync(requestRef, strategy);
        } else {
            return executeSync(requestRef, strategy);
        }
    }

    @Override
    public RefExecutionState state(RefExecutionAction action) throws ExternalOperationFailedException {
        VisualisRefExecutionAction visualisRefExecutionAction = ((VisualisRefExecutionAction)action);
        if(isAsyncExecution(visualisRefExecutionAction)) {
            AsyncExecutionOperationStrategy strategy = (AsyncExecutionOperationStrategy) visualisRefExecutionAction.getStrategy();
            return strategy.state(visualisRefExecutionAction.getRequestRef(), visualisRefExecutionAction.getId());
        } else {
            logger.warn("{} do not support async execution, turn async state to success to execute it.", visualisRefExecutionAction.getRequestRef().getType());
            visualisRefExecutionAction.getExecutionRequestRefContext().appendLog("do not support async execution, turn async state to success to execute it.");
            return RefExecutionState.Success;
        }
    }

    @Override
    public ExecutionResponseRef result(RefExecutionAction action) throws ExternalOperationFailedException {
        VisualisRefExecutionAction visualisRefExecutionAction = ((VisualisRefExecutionAction)action);
        if(isAsyncExecution(visualisRefExecutionAction)) {
            AsyncExecutionOperationStrategy strategy = (AsyncExecutionOperationStrategy) visualisRefExecutionAction.getStrategy();
            return strategy.getAsyncResult(visualisRefExecutionAction.getRequestRef(), visualisRefExecutionAction.getId());
        } else {
            ResponseRef responseRef = visualisRefExecutionAction.getStrategy()
                    .executeRef(visualisRefExecutionAction.getRequestRef());
            return ExecutionResponseRef.newBuilder().setResponseRef(responseRef).build();
        }
    }

    /**
     * I override this method, since I want to use SSORequestOperation to request Visualis server.
     * @return visualis appConn name
     */
    @Override
    protected String getAppConnName() {
        return VisualisAppConn.VISUALIS_APPCONN_NAME;
    }

    private RefExecutionAction executeSync(RefExecutionRequestRef.RefExecutionProjectWithContextRequestRef requestRef,
                                           OperationStrategy strategy) {
        return createRefExecutionAction(requestRef, "-1", strategy);
    }

    private boolean isAsyncExecution(VisualisRefExecutionAction action) {
        return !action.getId().equals("-1");
    }

    private RefExecutionAction createRefExecutionAction(RefExecutionRequestRef.RefExecutionProjectWithContextRequestRef requestRef,
                                                        String id, OperationStrategy strategy) {
        VisualisRefExecutionAction action = new VisualisRefExecutionAction();
        action.setRequestRef(requestRef);
        action.setExecutionRequestRefContext(requestRef.getExecutionRequestRefContext());
        action.setId(id);
        action.setStrategy(strategy);
        return action;
    }

    /**
     * 执行异步操作
     *
     * @param requestRef
     * @return
     */
    private RefExecutionAction executeAsync(RefExecutionRequestRef.RefExecutionProjectWithContextRequestRef requestRef,
                                            OperationStrategy strategy) throws ExternalOperationFailedException {
        AsyncExecutionOperationStrategy asyncStrategy = (AsyncExecutionOperationStrategy) strategy;
        //任务发布
        String execId = asyncStrategy.submit(requestRef);
        return createRefExecutionAction(requestRef, execId, asyncStrategy);
    }

    private boolean isSupportAsyncExecution(RefExecutionRequestRef.RefExecutionProjectWithContextRequestRef ref,
                                     OperationStrategy strategy) throws ExternalOperationFailedException {

        if(!(strategy instanceof ViewOptStrategy)) {
            return false;
        }
        ViewOptStrategy viewStrategy = (ViewOptStrategy) strategy;
        //判断数据源是不是hiveDatesouce，异步只支持hiveDatesouce，不支持jdbc
        String url = URLUtils.getUrl(getBaseUrl(), URLUtils.VIEW_DATA_URL_IS__HIVE_DATA_SOURCE, viewStrategy.getId(ref.getRefJobContent()));
        ref.getExecutionRequestRefContext().appendLog("dss execute view node, judge dataSource type from  " + url);
        DSSGetAction visualisGetAction = new DSSGetAction();
        visualisGetAction.setUser(ref.getUserName());
        visualisGetAction.setParameter("labels", ref.getDSSLabels().get(0).getValue().get("DSSEnv"));
        ResponseRef responseRef = VisualisCommonUtil.getExternalResponseRef(ref, ssoRequestOperation, url, visualisGetAction);
        return (boolean) responseRef.getValue("isLinkisDataSource");
    }
}
