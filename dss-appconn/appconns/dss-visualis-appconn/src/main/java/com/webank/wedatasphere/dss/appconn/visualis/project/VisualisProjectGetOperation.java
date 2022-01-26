package com.webank.wedatasphere.dss.appconn.visualis.project;

import com.webank.wedatasphere.dss.appconn.visualis.model.VisualisGetAction;
import com.webank.wedatasphere.dss.appconn.visualis.utils.URLUtils;
import com.webank.wedatasphere.dss.appconn.visualis.utils.VisualisCommonUtil;
import com.webank.wedatasphere.dss.common.entity.project.DSSProject;
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestOperation;
import com.webank.wedatasphere.dss.standard.app.structure.StructureService;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectGetOperation;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectRequestRef;
import com.webank.wedatasphere.dss.standard.common.exception.AppStandardErrorException;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import org.apache.linkis.httpclient.request.HttpAction;
import org.apache.linkis.httpclient.response.HttpResult;

import java.util.Map;

/**
 * Description
 *
 * @Author elishazhang
 * @Date 2021/11/11
 */

public class VisualisProjectGetOperation implements ProjectGetOperation {
    private SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation;
    private StructureService structureService;

    public VisualisProjectGetOperation(StructureService service, SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation) {
        this.structureService = service;
        this.ssoRequestOperation = ssoRequestOperation;
    }

    @Override
    public DSSProject getProject(ProjectRequestRef projectRef) throws ExternalOperationFailedException {
        String url = getBaseUrl() + URLUtils.PROJECT_SEARCH_URL;
        VisualisGetAction visualisGetAction = new VisualisGetAction();
        visualisGetAction.setUser(projectRef.getCreateBy());
        visualisGetAction.setParameter("keywords", projectRef.getName());
        try {
            HttpResult httpResult = VisualisCommonUtil.getHttpResult(visualisGetAction, url, projectRef, ssoRequestOperation);
            Map resMap = VisualisCommonUtil.getResponseMap(httpResult);
            VisualisCommonUtil.checkResponseMap(resMap);

            DSSProject dssProject = new DSSProject();
            Map<String, Object> payloadMap = (Map<String, Object>) resMap.get("payload");
            dssProject.setId(Long.parseLong(payloadMap.get("id").toString()));
            return dssProject;
        } catch (AppStandardErrorException e) {
            throw new ExternalOperationFailedException(90176, "search Visualis Project failed when get HttpResult", e);
        } catch (Exception e) {
            throw new ExternalOperationFailedException(90176, "search visualis project failed when parse response json", e);
        }
    }

    @Override
    public void init() {

    }

    @Override
    public void setStructureService(StructureService service) {
        this.structureService = service;
    }

    private String getBaseUrl() {
        return structureService.getAppInstance().getBaseUrl();
    }
}
