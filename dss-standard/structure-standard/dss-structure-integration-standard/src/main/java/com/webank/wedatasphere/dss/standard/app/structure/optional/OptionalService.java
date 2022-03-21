package com.webank.wedatasphere.dss.standard.app.structure.optional;

import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestService;
import com.webank.wedatasphere.dss.standard.app.structure.OptionalIntegrationStandard;
import com.webank.wedatasphere.dss.standard.common.app.AppIntegrationServiceImpl;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.dss.standard.common.utils.AppStandardClassUtils;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author enjoyyin
 * @date 2022-03-18
 * @since 0.5.0
 */
public class OptionalService extends AppIntegrationServiceImpl<SSORequestService> {

    private List<OptionalOperation> operations;
    private OptionalIntegrationStandard appStandard;

    public void init() {
        operations = AppStandardClassUtils.getInstance(appStandard.getAppConnName()).getInstances(OptionalOperation.class);
        operations.forEach(optionalOperation -> {
            optionalOperation.setOptionalService(this);
            optionalOperation.init();
        });
        LoggerFactory.getLogger(OptionalService.class).info("The AppConn {} initialized a list of OptionalOperations {}.",
                getAppInstance().getBaseUrl(), operations.stream().map(OptionalOperation::getOperationName).collect(Collectors.joining(", ")));
    }

    public OptionalOperation getOptionalOperation(String operationName) {
        return operations.stream().filter(optionalOperation -> optionalOperation.getOperationName().equals(operationName))
                .findAny().orElseThrow(() -> new ExternalOperationFailedException(50322, "Not exists Optional Operation " + operationName));
    }

    public void setAppStandard(OptionalIntegrationStandard appStandard) {
        this.appStandard = appStandard;
    }

    public OptionalIntegrationStandard getAppStandard() {
        return appStandard;
    }

}
