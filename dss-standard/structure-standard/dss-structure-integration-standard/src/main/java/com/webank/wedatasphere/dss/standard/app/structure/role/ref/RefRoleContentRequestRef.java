package com.webank.wedatasphere.dss.standard.app.structure.role.ref;

/**
 * @author enjoyyin
 * @since 1.1.0
 */
public interface RefRoleContentRequestRef<R extends RefRoleContentRequestRef<R>>
        extends RoleRequestRef<R> {

    default Long getRefRoleId() {
        return (Long) getParameter("refRoleId");
    }

    default R setRefRoleId(Long refRoleId) {
        setParameter("refRoleId", refRoleId);
        return (R) this;
    }

    default String getRoleName() {
        return (String) getParameter("roleName");
    }

    default R setRoleName(String roleName) {
        setParameter("roleName", roleName);
        return (R) this;
    }

}
