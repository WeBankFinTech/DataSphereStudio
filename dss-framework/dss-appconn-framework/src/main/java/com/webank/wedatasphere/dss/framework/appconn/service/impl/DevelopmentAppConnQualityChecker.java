package com.webank.wedatasphere.dss.framework.appconn.service.impl;

import com.webank.wedatasphere.dss.appconn.core.AppConn;
import com.webank.wedatasphere.dss.appconn.core.ext.OnlyDevelopmentAppConn;
import com.webank.wedatasphere.dss.appconn.core.ext.OnlyStructureAppConn;
import com.webank.wedatasphere.dss.framework.appconn.exception.AppConnQualityErrorException;
import com.webank.wedatasphere.dss.framework.appconn.service.AppConnQualityChecker;
import com.webank.wedatasphere.dss.standard.app.development.standard.DevelopmentIntegrationStandard;
import com.webank.wedatasphere.dss.standard.common.desc.AppInstance;

/**
 * @author enjoyyin
 * @date 2022-04-14
 * @since 0.5.0
 */
public class DevelopmentAppConnQualityChecker extends AbstractAppConnQualityChecker {

    @Override
    public void checkQuality(AppConn appConn) throws AppConnQualityErrorException {
        if(!(appConn instanceof OnlyDevelopmentAppConn)) {
            return;
        }
        String appConnName = appConn.getAppDesc().getAppName();
        checkAppInstance(appConn);
        AppInstance appInstance = appConn.getAppDesc().getAppInstances().get(0);
        DevelopmentIntegrationStandard developmentIntegrationStandard = ((OnlyDevelopmentAppConn) appConn).getOrCreateDevelopmentStandard();
        checkNull(developmentIntegrationStandard, appConnName, "developmentStandard");
        checkNull(developmentIntegrationStandard.getRefExecutionService(appInstance), appConnName, "refExecutionService");
    }

}
