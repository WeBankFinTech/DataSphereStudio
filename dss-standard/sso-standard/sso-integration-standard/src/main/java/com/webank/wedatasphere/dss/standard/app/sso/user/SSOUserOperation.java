package com.webank.wedatasphere.dss.standard.app.sso.user;

import com.webank.wedatasphere.dss.standard.app.sso.operation.AbstractOperation;
import com.webank.wedatasphere.dss.standard.app.sso.user.impl.SSOUserServiceImpl;
import com.webank.wedatasphere.dss.standard.common.entity.ref.RequestRef;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.service.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author enjoyyin
 * @date 2022-04-26
 * @since 1.1.0
 */
public abstract class SSOUserOperation<K extends RequestRef, V extends ResponseRef> extends AbstractOperation<K, V>
        implements Operation<K, V> {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    public final void setSSOUserService(SSOUserService service) {
        this.service = service;
    }

    @Override
    protected String getAppConnName() {
        if(service instanceof SSOUserServiceImpl) {
            return ((SSOUserServiceImpl) service).getAppConnName();
        }
        return null;
    }
}
