package com.webank.wedatasphere.dss.standard.app.sso.user;

import com.webank.wedatasphere.dss.standard.app.sso.user.ref.DSSUserContentRequestRef;
import com.webank.wedatasphere.dss.standard.app.sso.user.ref.RefUserContentResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;

/**
 * @author enjoyyin
 * @date 2022-04-25
 * @since 1.1.0
 */
public abstract class SSOUserCreationOperation<R extends DSSUserContentRequestRef<R>>
        extends SSOUserOperation<R, RefUserContentResponseRef> {

    public abstract RefUserContentResponseRef createUser(R requestRef) throws ExternalOperationFailedException;

}
