package com.webank.wedatasphere.dss.standard.app.development.ref;

/**
 * @author enjoyyin
 * @date 2022-03-09
 * @since 1.0.0
 */
public interface ProjectRefRequestRef<R extends ProjectRefRequestRef<R>> extends DevelopmentRequestRef<R> {

    default R setRefProjectId(Long refProjectId) {
        this.setParameter("refProjectId", refProjectId);
        return (R) this;
    }

    /**
     * 返回的是第三方 AppConn 的工程 Id，由第三方 AppConn {@code ProjectCreationOperation} 的实现类返回的 refProjectId。
     * @return 返回的是第三方 AppConn 的工程Id
     */
    default Long getRefProjectId() {
        return (Long) this.getParameter("refProjectId");
    }

    default R setDSSProjectId(Long dssProjectId) {
        this.setParameter("dssProjectId", dssProjectId);
        return (R) this;
    }

    /**
     * 返回的是 DSSProject 的工程 Id
     * @return 返回的是 DSSProject 的工程 Id
     */
    default Long getDSSProjectId() {
        return (Long) this.getParameter("dssProjectId");
    }

    default R setProjectName(String projectName) {
        this.setParameter("projectName", projectName);
        return (R) this;
    }

    /**
     * 返回的是 DSSProject 的工程名
     * @return 返回的是 DSSProject 的工程名
     */
    default String getProjectName() {
        return (String) this.getParameter("projectName");
    }

}
