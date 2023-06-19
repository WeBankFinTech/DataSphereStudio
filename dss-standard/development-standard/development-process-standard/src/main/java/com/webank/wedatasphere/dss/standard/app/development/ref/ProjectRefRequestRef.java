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
     * 特别注意：当dss调用三方appconn的节点拷贝接口时，若工作流是在同一个项目内拷贝，则refProjectId代表的是节点源工程Id；
     * 若工作流是跨工程拷贝，则refProjectId代表的是目标工程id。
     * 三方接入系统在实现节点拷贝操作时，需要在自己系统内先根据节点名获取源工程Id，判断和dss传入的refProjectId是否一致。
     * 若一致，则代表dss端发起的请求是工程内拷贝；若不一致，则代表dss端发起的请求是跨工程拷贝。三方系统在实现节点拷贝操作时需要根据此两种情况做不同实现。
     *
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
