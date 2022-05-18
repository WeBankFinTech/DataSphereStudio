package com.webank.wedatasphere.dss.standard.app.structure.optional;

import com.webank.wedatasphere.dss.standard.app.sso.operation.AbstractOperation;
import com.webank.wedatasphere.dss.standard.common.entity.ref.RequestRef;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;

/**
 * @author enjoyyin
 * @date 2022-03-18
 * @since 0.5.0
 */
public abstract class AbstractOptionalOperation<K extends RequestRef, V extends ResponseRef>
        extends AbstractOperation<K, V> implements OptionalOperation<K, V> {

    /**
     * for more detail, please access the super explanation.
     * @return null if you don't want to use SSORequestOperation; otherwise, a AppConn name is needed.
     */
    @Override
    protected String getAppConnName() {
        return null;
    }

    @Override
    public final void setOptionalService(OptionalService optionalService) {
        service = optionalService;
    }

}
