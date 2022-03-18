package com.webank.wedatasphere.dss.appconn.dolphinscheduler.sso;

import com.webank.wedatasphere.dss.standard.app.sso.SSOIntegrationStandard;
import com.webank.wedatasphere.dss.standard.app.sso.SSOIntegrationStandardFactory;
import com.webank.wedatasphere.dss.standard.app.sso.origin.HttpSSOIntegrationStandard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author enjoyyin
 * @date 2022-03-17
 * @since 1.1.0
 */
public class DolphinSchedulerSSOIntegrationStandardFactory implements SSOIntegrationStandardFactory {

    private SSOIntegrationStandard ssoIntegrationStandard;
    private Logger logger = LoggerFactory.getLogger(DolphinSchedulerSSOIntegrationStandardFactory.class);

    @Override
    public void init() {
        ssoIntegrationStandard = new HttpSSOIntegrationStandard();
        logger.info("DolphinScheduler AppConn will use {} to integrate with DSS in 1st SSO standard.", ssoIntegrationStandard.getClass().getName());
    }

    @Override
    public SSOIntegrationStandard getSSOIntegrationStandard() {
        return ssoIntegrationStandard;
    }
}
