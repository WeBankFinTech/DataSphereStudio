package com.webank.wedatasphere.dss.standard.app.structure.project.ref;

import com.webank.wedatasphere.dss.standard.app.structure.StructureRequestRefImpl;

/**
 * @author enjoyyin
 * @date 2022-03-13
 * @since 0.5.0
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

    class ProjectUpdateRequestRefImpl extends StructureRequestRefImpl<ProjectUpdateRequestRefImpl>
            implements ProjectUpdateRequestRef<ProjectUpdateRequestRefImpl> {}

}
