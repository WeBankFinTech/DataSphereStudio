package com.webank.wedatasphere.dss.standard.app.sso.user;

import com.webank.wedatasphere.dss.standard.app.sso.user.ref.DSSUserContentRequestRef;
import com.webank.wedatasphere.dss.standard.app.sso.user.ref.RefUserContentResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;

/**
 * @author enjoyyin
 * @date 2022-04-25
 * @since 1.1.0
 */
public abstract class SSOUserGetOperation<R extends DSSUserContentRequestRef<R>>
        extends SSOUserOperation<R, RefUserContentResponseRef> {

    /**
     * 请求第三方 AppConn，获取唯一英文用户名为 username 的 第三方 AppConn 用户信息。
     * 如果第三方 AppConn 不存在该用户，请将 refUserId 置为 null。
     * @param requestRef
     * @return 第三方 AppConn 用户信息，第三方 AppConn 不存在该用户，请将 refUserId 置为 null。
     * @throws ExternalOperationFailedException
     */
    public abstract RefUserContentResponseRef getUser(R requestRef) throws ExternalOperationFailedException;

}
