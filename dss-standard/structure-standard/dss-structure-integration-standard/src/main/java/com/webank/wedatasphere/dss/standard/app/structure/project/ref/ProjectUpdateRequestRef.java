package com.webank.wedatasphere.dss.standard.app.structure.project.ref;

import com.webank.wedatasphere.dss.standard.app.structure.StructureRequestRefImpl;

/**
 * @author enjoyyin
 * @date 2022-03-13
 * @since 1.1.0
 */
public interface ProjectUpdateRequestRef<R extends ProjectUpdateRequestRef<R>>
        extends DSSProjectContentRequestRef<R>, RefProjectContentRequestRef<R> {

    @Override
    default String getProjectName() {
        return getDSSProject().getName();
    }

    @Override
    default R setProjectName(String projectName) {
        getDSSProject().setName(projectName);
        return (R) this;
    }

    /**
     * 只包含本次新增的 DSS 工程相关权限用户
     * @return
     */
    default DSSProjectPrivilege getAddedDSSProjectPrivilege() {
        return (DSSProjectPrivilege) this.getParameter("addedDSSProjectPrivilege");
    }

    default R setAddedDSSProjectPrivilege(DSSProjectPrivilege addedDSSProjectPrivilege) {
        setParameter("addedDSSProjectPrivilege", addedDSSProjectPrivilege);
        return (R) this;
    }

    /**
     * 只包含本次移除的 DSS 工程相关权限用户
     * @return
     */
    default DSSProjectPrivilege getRemovedDSSProjectPrivilege() {
        return (DSSProjectPrivilege) this.getParameter("removedDSSProjectPrivilege");
    }

    default R setRemovedDSSProjectPrivilege(DSSProjectPrivilege removedDSSProjectPrivilege) {
        setParameter("removedDSSProjectPrivilege", removedDSSProjectPrivilege);
        return (R) this;
    }

    class ProjectUpdateRequestRefImpl extends StructureRequestRefImpl<ProjectUpdateRequestRefImpl>
            implements ProjectUpdateRequestRef<ProjectUpdateRequestRefImpl> {}

}
