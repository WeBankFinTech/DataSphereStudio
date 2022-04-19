package com.webank.wedatasphere.dss.standard.app.structure.project.ref;

import com.webank.wedatasphere.dss.common.entity.project.DSSProject;
import com.webank.wedatasphere.dss.standard.app.structure.StructureRequestRef;
import com.webank.wedatasphere.dss.standard.app.structure.StructureRequestRefImpl;

/**
 * @author enjoyyin
 * @date 2022-03-13
 * @since 0.5.0
 */
public interface DSSProjectContentRequestRef<R extends DSSProjectContentRequestRef<R>>
        extends StructureRequestRef<R> {

    default DSSProject getDSSProject() {
        return (DSSProject) this.getParameter("dssProject");
    }

    default R setDSSProject(DSSProject dssProject) {
        setParameter("dssProject", dssProject);
        return (R) this;
    }

    /**
     * DSS 工程的全量最新权限信息，包含了 DSS 工程所有的最新权限信息
     * @return DSSProjectPrivilege
     */
    default DSSProjectPrivilege getDSSProjectPrivilege() {
        return (DSSProjectPrivilege) this.getParameter("dssProjectPrivilege");
    }

    default R setDSSProjectPrivilege(DSSProjectPrivilege dssProjectPrivilege) {
        setParameter("dssProjectPrivilege", dssProjectPrivilege);
        return (R) this;
    }

    class DSSProjectContentRequestRefImpl
            extends StructureRequestRefImpl<DSSProjectContentRequestRefImpl>
            implements DSSProjectContentRequestRef<DSSProjectContentRequestRefImpl> {
    }
    
}
