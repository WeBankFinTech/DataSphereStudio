package com.webank.wedatasphere.dss.standard.app.structure.project.ref;

import com.webank.wedatasphere.dss.common.entity.project.DSSProject;
import com.webank.wedatasphere.dss.standard.app.structure.StructureRequestRef;
import com.webank.wedatasphere.dss.standard.app.structure.StructureRequestRefImpl;

import java.util.List;

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

    default List<String> getAccessUsers() {
        return (List<String>) this.getParameter("accessUsers");
    }

    default R setAccessUsers(List<String> accessUsers) {
        setParameter("accessUsers", accessUsers);
        return (R) this;
    }

    default List<String> getEditUsers() {
        return (List<String>) this.getParameter("editUsers");
    }

    default R setEditUsers(List<String> editUsers) {
        setParameter("editUsers", editUsers);
        return (R) this;
    }

    default List<String> getReleaseUsers() {
        return (List<String>) this.getParameter("releaseUsers");
    }

    default R setReleaseUsers(List<String> releaseUsers) {
        setParameter("releaseUsers", releaseUsers);
        return (R) this;
    }

    class DSSProjectContentRequestRefImpl
            extends StructureRequestRefImpl<DSSProjectContentRequestRefImpl>
            implements DSSProjectContentRequestRef<DSSProjectContentRequestRefImpl> {
    }
    
}
