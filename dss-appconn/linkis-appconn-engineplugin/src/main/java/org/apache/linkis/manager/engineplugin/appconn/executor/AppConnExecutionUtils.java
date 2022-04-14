package org.apache.linkis.manager.engineplugin.appconn.executor;

import com.webank.wedatasphere.dss.common.label.DSSLabel;
import com.webank.wedatasphere.dss.standard.app.development.listener.ref.RefExecutionRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefExecutionOperation;
import com.webank.wedatasphere.dss.standard.app.development.service.RefExecutionService;
import com.webank.wedatasphere.dss.standard.app.development.utils.DevelopmentOperationUtils;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;

import java.util.List;
import java.util.Map;

/**
 * @author enjoyyin
 * @date 2022-03-18
 * @since 0.5.0
 */
public class AppConnExecutionUtils {

    public static ResponseRef tryToOperation(RefExecutionService refExecutionService, String contextId, String projectName,
                                      ExecutionRequestRefContextImpl executionRequestRefContext, List<DSSLabel> dssLabels,
                                      String name, String type, String userName, Workspace workspace,
                                      Map<String, Object> refJobContent, Map<String, Object> variables) {
        return DevelopmentOperationUtils.tryRefJobContentRequestRefOperation(() -> refExecutionService,
                developmentService -> refExecutionService.getRefExecutionOperation(),
                refJobContentRequestRef -> refJobContentRequestRef.setRefJobContent(refJobContent),
                dssContextRequestRef -> dssContextRequestRef.setContextId(contextId),
                projectRefRequestRef -> projectRefRequestRef.setProjectName(projectName),
                (developmentOperation, developmentRequestRef) -> {
                    RefExecutionRequestRef requestRef = (RefExecutionRequestRef) developmentRequestRef;
                    requestRef.setExecutionRequestRefContext(executionRequestRefContext)
                        .setVariables(variables)
                        .setDSSLabels(dssLabels)
                        .setName(name)
                        .setType(type)
                        .setUserName(userName)
                        .setWorkspace(workspace);
                    return ((RefExecutionOperation) developmentOperation).execute(requestRef);
        }, "execute node " + name + " with type " + type);
    }

}
