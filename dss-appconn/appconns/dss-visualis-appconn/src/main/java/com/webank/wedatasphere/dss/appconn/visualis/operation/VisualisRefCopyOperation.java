package com.webank.wedatasphere.dss.appconn.visualis.operation;

import com.webank.wedatasphere.dss.appconn.visualis.VisualisAppConn;
import com.webank.wedatasphere.dss.appconn.visualis.model.VisualisPostAction;
import com.webank.wedatasphere.dss.appconn.visualis.ref.VisualisCopyRequestRef;
import com.webank.wedatasphere.dss.appconn.visualis.utils.URLUtils;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefCopyOperation;
import com.webank.wedatasphere.dss.standard.app.development.service.DevelopmentService;
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestOperation;
import com.webank.wedatasphere.dss.standard.common.desc.AppInstance;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import org.apache.linkis.httpclient.request.HttpAction;
import org.apache.linkis.httpclient.response.HttpResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * created by cooperyang on 2021/8/2
 * Description:
 */
public class VisualisRefCopyOperation implements RefCopyOperation<VisualisCopyRequestRef> {

    private static final Logger LOG = LoggerFactory.getLogger(VisualisRefCopyOperation.class);

    private DevelopmentService developmentService;

    private AppInstance appInstance;

    private SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation;

    @SuppressWarnings("unchecked")
    public VisualisRefCopyOperation(AppInstance appInstance, DevelopmentService developmentService) {
        this.appInstance = appInstance;
        this.developmentService = developmentService;
        this.ssoRequestOperation = this.developmentService.getSSORequestService().createSSORequestOperation(VisualisAppConn.VISUALIS_APPCONN_NAME);
    }


    @Override
    @SuppressWarnings("unchecked")
    public ResponseRef copyRef(VisualisCopyRequestRef requestRef) throws ExternalOperationFailedException {
        String url = getBaseUrl() + URLUtils.PROJECT_COPY_URL;
        Map<String, Object> jobContent = (Map<String, Object>) requestRef.getParameter("jobContent");
        String nodeType = requestRef.getParameter("nodeType").toString().toLowerCase();
        LOG.info("copy ref jobcontent: {}", jobContent);

        VisualisPostAction visualisPostAction = new VisualisPostAction();
        visualisPostAction.setUser(requestRef.getParameter("user").toString());
        visualisPostAction.addRequestPayload("projectVersion", "v1");
        visualisPostAction.addRequestPayload("flowVersion", requestRef.getParameter("orcVersion"));
        visualisPostAction.addRequestPayload("contextID", requestRef.getParameter("contextId").toString());

        return ModuleFactory.getInstance().crateModule(nodeType).copyRef(requestRef, jobContent, url, nodeType, visualisPostAction, ssoRequestOperation);


    }

    private String getBaseUrl() {
        return developmentService.getAppInstance().getBaseUrl();
    }

    @Override
    public void setDevelopmentService(DevelopmentService service) {
        this.developmentService = service;
    }
}
