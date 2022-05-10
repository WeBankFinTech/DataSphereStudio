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
 * 为第三方 AppConn 提供的通用 Service 服务，用于构建一些具备特殊能力的 Operation，以供DSS内嵌的应用工具使用。
 * @author enjoyyin
 * @date 2022-03-18
 * @since 1.1.0
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
