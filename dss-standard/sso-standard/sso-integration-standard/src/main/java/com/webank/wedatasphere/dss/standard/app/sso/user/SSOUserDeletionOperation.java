package com.webank.wedatasphere.dss.standard.app.sso.user;

import com.webank.wedatasphere.dss.standard.app.sso.user.ref.DSSUserContentRequestRef;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;

/**
 * @author enjoyyin
 * @date 2022-04-25
 * @since 1.1.0
 */
public abstract class SSOUserDeletionOperation<R extends DSSUserContentRequestRef<R>>
        extends SSOUserOperation<R, ResponseRef> {

    /**
     * 该方法暂时尚未被使用。
     * 删除用户操作，当 DSS Admin 模块删除一个用户时，同步请求第三方 AppConn 删除一个用户。
     * @param requestRef
     * @return 如果删除成功，请返回 ResponseRef.newBuilder().success()
     * @throws ExternalOperationFailedException 删除失败时抛出该异常
     */
    public abstract ResponseRef deleteUser(R requestRef) throws ExternalOperationFailedException;

}
