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

import com.webank.wedatasphere.dss.appconn.visualis.ViewAsyncRefExecutionOperation;
import com.webank.wedatasphere.dss.appconn.visualis.ViewExecutionAction;
import com.webank.wedatasphere.dss.appconn.visualis.VisualisAppConn;
import com.webank.wedatasphere.dss.appconn.visualis.constant.VisualisConstant;
import com.webank.wedatasphere.dss.appconn.visualis.enums.ModuleEnum;
import com.webank.wedatasphere.dss.appconn.visualis.utils.HttpUtils;
import com.webank.wedatasphere.dss.appconn.visualis.utils.URLUtils;
import com.webank.wedatasphere.dss.appconn.visualis.utils.VisualisCommonUtil;
import com.webank.wedatasphere.dss.appconn.visualis.utils.VisualisDownloadAction;
import com.webank.wedatasphere.dss.standard.app.development.listener.common.*;
import com.webank.wedatasphere.dss.standard.app.development.listener.core.LongTermRefExecutionOperation;
import com.webank.wedatasphere.dss.standard.app.development.listener.core.SchedulerManager;
import com.webank.wedatasphere.dss.standard.app.development.listener.scheduler.LongTermRefExecutionScheduler;
import com.webank.wedatasphere.dss.standard.app.development.service.DevelopmentService;
import com.webank.wedatasphere.dss.standard.app.development.ref.ExecutionRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefExecutionOperation;
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestOperation;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.AppStandardErrorException;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import org.apache.linkis.httpclient.request.HttpAction;
import org.apache.linkis.httpclient.response.HttpResult;
import org.apache.linkis.server.BDPJettyServerHelper;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Map;
import java.util.Optional;

public class VisualisRefExecutionOperation extends LongTermRefExecutionOperation implements RefExecutionOperation {

    private final static Logger logger = LoggerFactory.getLogger(VisualisRefExecutionOperation.class);
    private DevelopmentService developmentService;
    private SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation;
    private LongTermRefExecutionScheduler scheduler = SchedulerManager.getScheduler();

    public VisualisRefExecutionOperation(DevelopmentService service) {
        this.developmentService = service;
        this.ssoRequestOperation = this.developmentService.getSSORequestService().createSSORequestOperation(VisualisAppConn.VISUALIS_APPCONN_NAME);
    }

    @Override
    protected RefExecutionAction submit(ExecutionRequestRef requestRef) {
        return new ViewAsyncRefExecutionOperation().execute(requestRef, HttpUtils.getBaseUrl(developmentService), ssoRequestOperation);
    }

    @Override
    public RefExecutionState state(RefExecutionAction action) {
        return new ViewAsyncRefExecutionOperation().state(action, HttpUtils.getBaseUrl(developmentService), ssoRequestOperation);
    }


    @Override
    public CompletedExecutionResponseRef result(RefExecutionAction action) {
        return new ViewAsyncRefExecutionOperation().result(action, HttpUtils.getBaseUrl(developmentService), ssoRequestOperation);
    }


    @Override
    public ResponseRef execute(ExecutionRequestRef ref) {
        AsyncExecutionRequestRef asyncExecutionRequestRef = (AsyncExecutionRequestRef) ref;
        String nodeType = asyncExecutionRequestRef.getExecutionRequestRefContext().getRuntimeMap().get("nodeType").toString().toLowerCase().trim();
        String nodeName = VisualisConstant.NODE_NAME_PREFIX + nodeType;
        logger.info("nodeName:{}", nodeName);
        if (ModuleEnum.VIEW.getName().equals(nodeName)) {
            try {
                return executeAsyncOpt(asyncExecutionRequestRef);
            } catch (ExternalOperationFailedException e) {
                asyncExecutionRequestRef.getExecutionRequestRefContext().appendLog(e.getMessage());
                logger.error("Async execute view node failed", e);
            }
        } else {
            return executeSyncOpt(ref, nodeName);
        }
        return null;
    }


    /**
     * 执行异步操作
     *
     * @param requestRef
     * @return
     */
    private ResponseRef executeAsyncOpt(ExecutionRequestRef requestRef) throws ExternalOperationFailedException {
        //判断数据源是不是hiveDatesouce，异步只支持hiveDatesouce，不支持jdbc
        OperationStrategy viewStrategy = ModuleFactory.getInstance().crateModule((ModuleEnum.VIEW.getName()));
        AsyncExecutionRequestRef asyncExecutionRequestRef = (AsyncExecutionRequestRef) requestRef;
        boolean hiveDataSource = isHiveDataSource(asyncExecutionRequestRef, HttpUtils.getBaseUrl(developmentService), ssoRequestOperation, viewStrategy);
        if (hiveDataSource) {
            //任务发布
            RefExecutionAction action = submit(requestRef);
            ViewExecutionAction viewAction = null;
            if (action instanceof ViewExecutionAction) {
                viewAction = (ViewExecutionAction) action;
            }
            if (action instanceof AbstractRefExecutionAction) {
                ((AbstractRefExecutionAction) action).setExecutionRequestRefContext(createExecutionRequestRefContext(requestRef));
            }
            //获取任务状态
            RefExecutionState state = state(viewAction);
            if (state != null && state.isCompleted()) {
                //获取结果集
                CompletedExecutionResponseRef response = result(viewAction);
                return response;
            } else {
                AsyncExecutionResponseRef response = createAsyncResponseRef(requestRef, action);
                response.setRefExecution(this);
                if (scheduler != null) {
                    scheduler.addAsyncResponse(response);
                }
                return response;
            }
        } else {
            return viewStrategy.executeRef(asyncExecutionRequestRef, HttpUtils.getBaseUrl(developmentService), ssoRequestOperation);
        }
    }

    private boolean isHiveDataSource(AsyncExecutionRequestRef ref,
                                     String baseUrl,
                                     SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation,
                                     OperationStrategy viewStrategy) throws ExternalOperationFailedException {

        String url = URLUtils.getUrl(baseUrl, URLUtils.VIEW_DATA_URL_IS__HIVE_DATA_SOURCE, viewStrategy.getId(ref));
        ref.getExecutionRequestRefContext().appendLog("dss execute view node,judge datasource type from  " + url);
        VisualisDownloadAction visualisDownloadAction = new VisualisDownloadAction();
        visualisDownloadAction.setUser(VisualisCommonUtil.getUser(ref));
        InputStream inputStream = null;
        try {
            VisualisCommonUtil.getHttpResult(ref, ssoRequestOperation, url, visualisDownloadAction);
            inputStream = Optional.of(visualisDownloadAction.getInputStream()).orElseThrow(() -> new ExternalOperationFailedException(90176, "DSS execute view node failed,inputStream is empty", null));
            Map map = BDPJettyServerHelper.gson().fromJson(IOUtils.toString(inputStream), Map.class);
            ref.getExecutionRequestRefContext().appendLog(map.toString());
            @SuppressWarnings("unchecked")
            Map dataMap = Optional.of((Map<String, Object>) map.get("data")).orElseThrow(() -> new ExternalOperationFailedException(90176, "judge datasource type failed"));
            return Optional.of(Boolean.parseBoolean(dataMap.get("isLinkisDataSource").toString())).orElseThrow(() -> new ExternalOperationFailedException(90176, "judge datasource type failed"));
        } catch (AppStandardErrorException e) {
            ref.getExecutionRequestRefContext().appendLog("dss execute view node,judge datasource type error");
            throw new ExternalOperationFailedException(90176, "dss execute view node,judge datasource failed", e);
        } catch (Exception e) {
            throw new ExternalOperationFailedException(90176, "dss execute view node,exec http request failed", e);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }


    /**
     * 执行同步操作
     *
     * @param ref
     * @param nodeName
     * @return
     */
    private ResponseRef executeSyncOpt(ExecutionRequestRef ref, String nodeName) {
        AsyncExecutionRequestRef asyncExecutionRequestRef = (AsyncExecutionRequestRef) ref;
        try {
            return ModuleFactory.getInstance().crateModule(nodeName).executeRef(asyncExecutionRequestRef, HttpUtils.getBaseUrl(developmentService), ssoRequestOperation);
        } catch (ExternalOperationFailedException e) {
            logger.error("execute " + nodeName + " failed", e);
        }
        return null;

    }

    @Override
    public void setDevelopmentService(DevelopmentService service) {
        this.developmentService = service;
    }


}
