package com.webank.wedatasphere.dss.standard.app.structure.project.ref;

import com.webank.wedatasphere.dss.standard.app.structure.StructureRequestRef;
import com.webank.wedatasphere.dss.standard.app.structure.StructureRequestRefImpl;

/**
 * @author enjoyyin
 * @date 2022-03-13
 * @since 0.5.0
 */
public interface RefProjectContentRequestRef<R extends RefProjectContentRequestRef<R>>
        extends StructureRequestRef<R> {

    default Long getRefProjectId() {
        return (Long) getParameter("refProjectId");
    }

    default R setRefProjectId(Long refProjectId) {
        setParameter("refProjectId", refProjectId);
        return (R) this;
    }

    default String getProjectName() {
        return (String) getParameter("projectName");
    }

    default R setProjectName(String projectName) {
        setParameter("projectName", projectName);
        return (R) this;
    }

    class RefProjectContentRequestRefImpl extends StructureRequestRefImpl<RefProjectContentRequestRefImpl>
        implements RefProjectContentRequestRef<RefProjectContentRequestRefImpl>{}

}
