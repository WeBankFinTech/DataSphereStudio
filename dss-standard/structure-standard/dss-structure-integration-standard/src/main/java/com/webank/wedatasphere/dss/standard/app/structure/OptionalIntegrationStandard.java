package com.webank.wedatasphere.dss.standard.app.structure;

import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestService;
import com.webank.wedatasphere.dss.standard.app.structure.optional.OptionalService;
import com.webank.wedatasphere.dss.standard.common.core.AbstractAppIntegrationStandard;
import com.webank.wedatasphere.dss.standard.common.desc.AppInstance;
import com.webank.wedatasphere.dss.standard.common.exception.AppStandardErrorException;

import java.util.HashMap;
import java.util.Map;

/**
 * 这是一个可选的 Standard，用于协助第三方AppConn想要实现一些特殊的 Operation，这些Operation本身并不属于
 * 三大规范强制要求要实现的 Operation，但是会提供给三大规范或是DSS内嵌的应用工具使用。
 * @author enjoyyin
 * @date 2022-03-18
 * @since 1.1.0
 */
public class OptionalIntegrationStandard extends AbstractAppIntegrationStandard<OptionalService, SSORequestService> {

    private static Map<String, OptionalIntegrationStandard> optionalIntegrationStandards = new HashMap<>();
    private String appConnName;

    public static OptionalIntegrationStandard getInstance(String appConnName) {
        if(!optionalIntegrationStandards.containsKey(appConnName)) {
            synchronized (OptionalIntegrationStandard.class) {
                if(!optionalIntegrationStandards.containsKey(appConnName)) {
                    OptionalIntegrationStandard standard = new OptionalIntegrationStandard();
                    standard.setAppConnName(appConnName);
                    optionalIntegrationStandards.put(appConnName, standard);
                }
            }
        }
        return optionalIntegrationStandards.get(appConnName);
    }

    public OptionalService getOptionalService(AppInstance appInstance) {
        return getOrCreate(appInstance, OptionalService::new, OptionalService.class);
    }

    public String getAppConnName() {
        return appConnName;
    }

    public void setAppConnName(String appConnName) {
        this.appConnName = appConnName;
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
    public void close() {

    }
}
