package com.webank.wedatasphere.dss.standard.app.structure.role.ref;

import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationWarnException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author enjoyyin
 * @since 1.1.0
 */
public interface RoleUpdateRequestRef<R extends RoleUpdateRequestRef<R>> extends
    DSSRoleContentRequestRef<R>, RefRoleContentRequestRef<R> {

    @Override
    default String getRoleName() {
        if(getRole() == null) {
            return null;
        }
        return getRole().getName();
    }

    @Override
    default R setRoleName(String roleName) {
        throw new ExternalOperationWarnException(90030, "not support method.");
    }

    /**
     * 该角色本次新增的所有用户列表
     * @return 本次新增的所有用户列表
     */
    default List<String> getAddedUserNames() {
        if(!getParameters().containsKey("addedUserNames")) {
            return new ArrayList<>();
        }
        return (List<String>) getParameter("addedUserNames");
    }

    default R setAddedUserNames(List<String> addedUserNames) {
        setParameter("addedUserNames", addedUserNames);
        return (R) this;
    }

    /**
     * 该角色本次删除的所有用户列表
     * @return 本次新增的所有用户列表
     */
    default List<String> getRemovedUserNames() {
        if(!getParameters().containsKey("removedUserNames")) {
            return new ArrayList<>();
        }
        return (List<String>) getParameter("removedUserNames");
    }

    default R setRemovedUserNames(List<String> removedUserNames) {
        setParameter("removedUserNames", removedUserNames);
        return (R) this;
    }

}
