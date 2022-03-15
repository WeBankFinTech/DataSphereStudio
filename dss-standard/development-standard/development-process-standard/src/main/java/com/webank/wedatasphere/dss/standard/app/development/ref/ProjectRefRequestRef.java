package com.webank.wedatasphere.dss.standard.app.development.ref;

/**
 * @author enjoyyin
 * @date 2022-03-09
 * @since 0.5.0
 */
public interface ProjectRefRequestRef<R extends ProjectRefRequestRef<R>> extends DevelopmentRequestRef<R> {

    default R setProjectRefId(Long projectRefId) {
        this.setParameter("projectRefId", projectRefId);
        return (R) this;
    }

    default Long getProjectRefId() {
        return (Long) this.getParameter("projectRefId");
    }

    default R setProjectName(String projectName) {
        this.setParameter("projectName", projectName);
        return (R) this;
    }

    default String getProjectName() {
        return (String) this.getParameter("projectName");
    }

}
