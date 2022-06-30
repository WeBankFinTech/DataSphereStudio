package com.webank.wedatasphere.dss.standard.app.sso.user.ref;

import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.standard.app.sso.ref.WorkspaceRequestRef;
import com.webank.wedatasphere.dss.standard.common.entity.ref.RequestRefImpl;

/**
 * @author enjoyyin
 * @date 2022-04-25
 * @since 1.1.0
 */
public interface DSSUserContentRequestRef<R extends DSSUserContentRequestRef<R>>
        extends WorkspaceRequestRef {

    default User getUser() {
        return (User) getParameter("user");
    }

    default R setUser(User user) {
        setParameter("user", user);
        return (R) this;
    }

    /**
     * 请注意，该方法返回的 workspace 不会带有 workspaceId 和 workspaceName。
     * 因为用户的 CRUD 与工作空间无关的，该 workspace 只是为了保证可以正常使用 SSORequestOperation
     * @return
     */
    @Override
    default Workspace getWorkspace() {
        return (Workspace) getParameter("workspace");
    }

    default R setWorkspace(Workspace workspace) {
        setParameter("workspace", workspace);
        return (R) this;
    }

    interface User {

        /**
         * 英文用户名，全局唯一
         * @return 英文用户名
         */
        String getUsername();

        /**
         * 中文用户名，允许重复
         * @return 中文用户名
         */
        String getName();

        /**
         * 是否是第一次登陆 DSS，一般为 true
         * @return 如果是第一次登陆 DSS，则为true
         */
        Boolean isFirstLogin();

        /**
         * 是否为超级管理员用户
         * @return 如果是超级管理员，则返回true
         */
        Boolean isAdmin();

    }

    class DSSUserContentRequestRefImpl extends RequestRefImpl
            implements DSSUserContentRequestRef<DSSUserContentRequestRefImpl> {
    }

}
