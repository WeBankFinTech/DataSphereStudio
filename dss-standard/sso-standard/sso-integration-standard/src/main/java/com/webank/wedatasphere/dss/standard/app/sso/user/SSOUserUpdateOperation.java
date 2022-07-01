package com.webank.wedatasphere.dss.standard.app.sso.user;

import com.webank.wedatasphere.dss.standard.app.sso.user.ref.DSSUserContentRequestRef;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;

/**
 * @author enjoyyin
 * @date 2022-04-25
 * @since 1.1.0
 */
public abstract class SSOUserUpdateOperation<R extends DSSUserContentRequestRef<R>>
        extends SSOUserOperation<R, ResponseRef> {

    /**
     * 更新用户操作，当 DSS Admin 模块更新一个用户时，同步请求第三方 AppConn 更新一个用户。
     * @param requestRef
     * @return 如果更新成功，请返回 ResponseRef.newBuilder().success()
     * @throws ExternalOperationFailedException
     */
    public abstract ResponseRef updateUser(R requestRef) throws ExternalOperationFailedException;

}