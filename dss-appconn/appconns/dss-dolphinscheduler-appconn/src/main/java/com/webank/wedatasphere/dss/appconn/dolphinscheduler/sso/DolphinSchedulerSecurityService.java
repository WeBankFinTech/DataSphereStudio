package com.webank.wedatasphere.dss.appconn.dolphinscheduler.sso;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.wedatasphere.dss.appconn.dolphinscheduler.operation.DolphinSchedulerTokenOperation;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefQueryOperation;
import com.webank.wedatasphere.dss.standard.app.development.ref.CommonRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.ref.impl.CommonRequestRefImpl;
import com.webank.wedatasphere.dss.standard.app.development.service.AbstractRefQueryService;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;

/**
 * The type Dolphin scheduler security service.
 *
 * @author yuxin.yuan
 * @date 2021/10/19
 */
public final class DolphinSchedulerSecurityService extends AbstractRefQueryService {

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
        CommonRequestRef requestRef = new CommonRequestRefImpl();
        requestRef.setParameter("userName", user);

        ResponseRef responseRef = getRefQueryOperation().query(requestRef);
        String token = (String)responseRef.getValue("token");
        if (token == null) {
            return "";
        }
        return token;
    }

    @Override
    protected RefQueryOperation createRefQueryOperation() {
        return DolphinSchedulerTokenOperation.getInstance(baseUrl);
    }

}
