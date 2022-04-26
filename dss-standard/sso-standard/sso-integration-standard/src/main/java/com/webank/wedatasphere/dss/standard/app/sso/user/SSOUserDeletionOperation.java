package com.webank.wedatasphere.dss.standard.app.sso.user;

import com.webank.wedatasphere.dss.standard.app.sso.user.ref.DSSUserContentRequestRef;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;

/**
 * @author enjoyyin
 * @date 2022-04-25
 * @since 0.5.0
 */
public abstract class SSOUserDeletionOperation<R extends DSSUserContentRequestRef<R>>
        extends SSOUserOperation<R, ResponseRef> {

    public abstract ResponseRef deleteUser(R requestRef) throws ExternalOperationFailedException;

}
