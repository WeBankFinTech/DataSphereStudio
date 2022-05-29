package com.webank.wedatasphere.dss.standard.app.development.operation;

import com.webank.wedatasphere.dss.standard.app.development.ref.DevelopmentRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.service.DevelopmentService;
import com.webank.wedatasphere.dss.standard.app.sso.operation.AbstractOperation;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author enjoyyin
 * @date 2022-03-09
 * @since 0.5.0
 */
public abstract class AbstractDevelopmentOperation<K extends DevelopmentRequestRef<K>, V extends ResponseRef>
        extends AbstractOperation<K, V> implements DevelopmentOperation<K, V> {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Override this method and return null by default.
     * The reason of returning null is that, it is possible that many third-part AppConn may not use SSORequestOperation
     * which dependents the HttpClient of Linkis
     * to request the third-part system, so override in this method to avoid to implement it in these subclasses.
     * <br>
     * Notice: if you want to use SSORequestOperation, please override this method.
     * @return null by default.
     */
    @Override
    protected String getAppConnName() {
        return null;
    }

    @Override
    public final void setDevelopmentService(DevelopmentService service) {
        this.service = service;
    }
}
