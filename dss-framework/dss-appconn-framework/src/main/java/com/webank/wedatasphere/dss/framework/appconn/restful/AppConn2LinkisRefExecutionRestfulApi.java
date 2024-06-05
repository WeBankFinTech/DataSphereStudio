package com.webank.wedatasphere.dss.framework.appconn.restful;

import com.webank.wedatasphere.dss.appconn.core.ext.OnlyDevelopmentAppConn;
import com.webank.wedatasphere.dss.appconn.manager.AppConnManager;
import com.webank.wedatasphere.dss.common.label.DSSLabel;
import com.webank.wedatasphere.dss.common.label.EnvDSSLabel;
import com.webank.wedatasphere.dss.common.utils.DSSCommonUtils;
import com.webank.wedatasphere.dss.standard.app.development.operation.AppConn2LinkisRefExecutionOperation;
import com.webank.wedatasphere.dss.standard.app.development.ref.AppConn2LinkisResponseRef;
import com.webank.wedatasphere.dss.standard.app.development.ref.RefJobContentRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.service.RefExecutionService;
import com.webank.wedatasphere.dss.standard.app.development.utils.DevelopmentOperationUtils;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.standard.common.desc.AppInstance;
import com.webank.wedatasphere.dss.standard.common.desc.AppInstanceImpl;
import org.apache.linkis.server.Message;
import org.apache.linkis.server.security.SecurityFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RequestMapping(path = "/dss/framework/appconn", produces = {"application/json"})
@RestController
public class AppConn2LinkisRefExecutionRestfulApi {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppConn2LinkisRefExecutionRestfulApi.class);

    private AppInstance emptyAppInstance = new AppInstanceImpl();

    // 仅仅用于表示依赖，希望AppConnManagerRestfulApi先完成所有AppConn的加载之后，本Restful再对外提供服务。
    @Autowired
    private AppConnManagerRestfulApi appConnManagerRestfulApi;

    @RequestMapping(path = "execute", method = RequestMethod.POST)
    public Message execute(HttpServletRequest request, @RequestBody Map<String, Object> json) {
        String userName = SecurityFilter.getLoginUsername(request);
        LOGGER.info("user {} try to transform jobContent to Linkis job with requestBody {}.", userName, json);
        String workspaceStr = (String) json.get("workspaceStr");
        String appConnName = (String) json.get("appConnName");
        String labelStr = (String) json.get("labels");
        Map<String, Object> refJobContent = (Map<String, Object>) json.get("jobContent");
        Workspace workspace = DSSCommonUtils.COMMON_GSON.fromJson(workspaceStr, Workspace.class);
        OnlyDevelopmentAppConn appConn = (OnlyDevelopmentAppConn) AppConnManager.getAppConnManager().getAppConn(appConnName);
        AppInstance appInstance;
        List<DSSLabel> labels = Arrays.asList(new EnvDSSLabel(labelStr));
        // 原则上，AppConn2Linkis类型的AppConn，要么一个AppInstance都没有，要么就需按照AppConn的规范去录入AppInstance.
        if(appConn.getAppDesc().getAppInstances().isEmpty()) {
            LOGGER.info("AppConn {} has no appInstance, use empty appInstance to get operation.", appConnName);
            appInstance = emptyAppInstance;
        } else {
            appInstance = appConn.getAppDesc().getAppInstancesByLabels(labels).get(0);
        }
        AppConn2LinkisResponseRef responseRef = DevelopmentOperationUtils.tryDevelopmentOperation(() -> appConn.getOrCreateDevelopmentStandard().getRefExecutionService(appInstance),
                developmentService -> ((RefExecutionService) developmentService).getRefExecutionOperation(),
                null, refJobContentRequestRef -> refJobContentRequestRef.setRefJobContent(refJobContent),
                null, null, (developmentOperation, developmentRequestRef) -> {
                    developmentRequestRef.setWorkspace(workspace).setUserName(userName).setDSSLabels(labels);
                    return ((AppConn2LinkisRefExecutionOperation) developmentOperation).execute((RefJobContentRequestRef) developmentRequestRef);
                }, null, "fetch linkis jobContent from appConn " + appConnName + " failed.");
        LOGGER.info("user {} transform jobContent from AppConn {} with responseRef {}.", userName, appConnName, DSSCommonUtils.COMMON_GSON.toJson(responseRef));
        return Message.ok().data("executionCode", responseRef.getCode()).data("params", responseRef.getParams())
                .data("engineType", responseRef.getEngineType()).data("runType", responseRef.getRunType());
    }

}
