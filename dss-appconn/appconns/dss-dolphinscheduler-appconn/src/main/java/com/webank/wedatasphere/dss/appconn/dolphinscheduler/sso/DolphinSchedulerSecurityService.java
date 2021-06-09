package com.webank.wedatasphere.dss.appconn.dolphinscheduler.sso;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.wedatasphere.dss.appconn.dolphinscheduler.operation.DolphinSchedulerTokenOperation;
import com.webank.wedatasphere.dss.standard.app.development.DevelopmentService;
import com.webank.wedatasphere.dss.standard.app.development.crud.CommonRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.query.RefQueryOperation;
import com.webank.wedatasphere.dss.standard.app.development.query.RefQueryService;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;

/**
 * The type Dolphin scheduler security service.
 *
 * @author yuxin.yuan
 * @date 2021/06/08
 */
public final class DolphinSchedulerSecurityService implements RefQueryService {

    private static final Logger logger = LoggerFactory.getLogger(DolphinSchedulerSecurityService.class);

    private static DolphinSchedulerSecurityService instance;

    private String baseUrl;

    private DolphinSchedulerSecurityService(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public static DolphinSchedulerSecurityService getInstance(String baseUrl) {
        if (null == instance) {
            synchronized (DolphinSchedulerSecurityService.class) {
                if (null == instance) {
                    instance = new DolphinSchedulerSecurityService(baseUrl);
                }
            }
        }
        return instance;
    }

    public String getUserToken(String user) throws ExternalOperationFailedException {
        CommonRequestRef requestRef = new CommonRequestRef();
        requestRef.setParameter("userName", user);

        ResponseRef responseRef = getRefQueryOperation().query(requestRef);
        String token = (String)responseRef.getValue("token");
        if (token == null) {
            return "";
        }
        return token;
    }

    @Override
    public RefQueryOperation getRefQueryOperation() {
        return DolphinSchedulerTokenOperation.getInstance(baseUrl);
    }

    @Override
    public DevelopmentService getDevelopmentService() {
        return null;
    }

    @Override
    public void setDevelopmentService(DevelopmentService developmentService) {

    }
}
