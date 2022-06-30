package com.webank.wedatasphere.dss.framework.appconn.service.impl;

import com.webank.wedatasphere.dss.appconn.core.AppConn;
import com.webank.wedatasphere.dss.appconn.core.ext.OnlyDevelopmentAppConn;
import com.webank.wedatasphere.dss.framework.appconn.conf.AppConnConf;
import com.webank.wedatasphere.dss.framework.appconn.exception.AppConnQualityErrorException;
import com.webank.wedatasphere.dss.standard.app.development.service.RefCRUDService;
import com.webank.wedatasphere.dss.standard.app.development.service.RefExecutionService;
import com.webank.wedatasphere.dss.standard.app.development.standard.DevelopmentIntegrationStandard;
import com.webank.wedatasphere.dss.standard.common.desc.AppInstance;
import org.springframework.stereotype.Component;

/**
 * @author enjoyyin
 * @date 2022-04-14
 * @since 1.1.0
 */
@Component
public class DevelopmentAppConnQualityChecker extends AbstractAppConnQualityChecker {

    public DevelopmentAppConnQualityChecker() {
        super(AppConnConf.DEVELOPMENT_QUALITY_CHECKER_IGNORE_LIST.getValue());
    }

    @Override
    protected void checkAppConnQuality(AppConn appConn) throws AppConnQualityErrorException {
        if(!(appConn instanceof OnlyDevelopmentAppConn)) {
            return;
        }
        String appConnName = appConn.getAppDesc().getAppName();
        checkAppInstance(appConn);
        AppInstance appInstance = appConn.getAppDesc().getAppInstances().get(0);
        DevelopmentIntegrationStandard developmentIntegrationStandard = ((OnlyDevelopmentAppConn) appConn).getOrCreateDevelopmentStandard();
        checkNull(developmentIntegrationStandard, appConnName, "developmentStandard");
        RefExecutionService refExecutionService = developmentIntegrationStandard.getRefExecutionService(appInstance);
        checkNull(refExecutionService, appConnName, "refExecutionService");
        checkNull(refExecutionService.getRefExecutionOperation(), appConnName, "refExecutionOperation");
        checkBoolean(developmentIntegrationStandard.getRefCRUDService(appInstance) != null &&
                developmentIntegrationStandard.getRefImportService(appInstance) == null, appConnName,
                "RefImportService is needed since refCRUDService is exists.");
        checkBoolean(developmentIntegrationStandard.getRefCRUDService(appInstance) != null &&
                        developmentIntegrationStandard.getRefExportService(appInstance) == null, appConnName,
                "RefExportService is needed since refCRUDService is exists.");
        RefCRUDService refCRUDService = developmentIntegrationStandard.getRefCRUDService(appInstance);
        if(refCRUDService == null) {
            return;
        }
        checkNull(refCRUDService.getRefUpdateOperation(), appConnName, "refUpdateOperation");
        checkNull(refCRUDService.getRefCopyOperation(), appConnName, "refCopyOperation");
        checkNull(refCRUDService.getRefDeletionOperation(), appConnName, "refDeletionOperation");
        checkNull(developmentIntegrationStandard.getRefImportService(appInstance).getRefImportOperation(), appConnName, "refImportOperation");
        checkNull(developmentIntegrationStandard.getRefExportService(appInstance).getRefExportOperation(), appConnName, "refExportOperation");
    }

}
