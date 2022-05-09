package com.webank.wedatasphere.dss.standard.app.structure.role.ref;

import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.standard.app.sso.ref.WorkspaceRequestRef;
import com.webank.wedatasphere.dss.standard.app.structure.StructureRequestRef;

/**
 * @author enjoyyin
 * @date 2022-05-07
 * @since 1.1.0
 */
public interface DSSRoleContentRequestRef<R extends DSSRoleContentRequestRef<R>>
        extends RoleRequestRef<R> {

    /**
     * 包含了 DSS {@code Role} 的信息
     * @return Role
     */
    default Role getRole() {
        return (Role) getParameter("role");
    }

    default R setRole(Role role) {
        setParameter("role", role);
        return (R) this;
    }

}
