package com.webank.wedatasphere.dss.standard.app.structure;

import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestService;
import com.webank.wedatasphere.dss.standard.app.structure.optional.OptionalService;
import com.webank.wedatasphere.dss.standard.common.core.AbstractAppIntegrationStandard;
import com.webank.wedatasphere.dss.standard.common.desc.AppInstance;
import com.webank.wedatasphere.dss.standard.common.exception.AppStandardErrorException;

import java.io.IOException;

/**
 * 这是一个可选的 Standard，用于协助第三方AppConn想要实现一些特殊的 Operation，这些Operation本身并不属于
 * 三大规范强制要求要实现的 Operation，但是会提供给三大规范或是DSS内嵌的应用工具使用。
 * @author enjoyyin
 * @date 2022-03-18
 * @since 1.1.0
 */
public class OptionalIntegrationStandard extends AbstractAppIntegrationStandard<OptionalService, SSORequestService> {

    private static OptionalIntegrationStandard optionalIntegrationStandard;

    public static OptionalIntegrationStandard getInstance() {
        if(optionalIntegrationStandard == null) {
            synchronized (OptionalIntegrationStandard.class) {
                if(optionalIntegrationStandard == null) {
                    optionalIntegrationStandard = new OptionalIntegrationStandard();
                }
            }
        }
        return optionalIntegrationStandard;
    }

    public OptionalService getOptionalService(AppInstance appInstance) {
        return getOrCreate(appInstance, OptionalService::new, OptionalService.class);
    }

    @Override
    protected <T extends OptionalService> void initService(T service) {
        service.setAppStandard(this);
        service.init();
    }

    @Override
    public String getStandardName() {
        return "OptionalIntegrationStandard";
    }

    @Override
    public int getGrade() {
        return 10;
    }

    @Override
    public void init() throws AppStandardErrorException {

    }

    @Override
    public void close() throws IOException {

    }
}
