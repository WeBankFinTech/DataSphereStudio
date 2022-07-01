package com.webank.wedatasphere.dss.standard.app.development.ref;

/**
 * @author enjoyyin
 * @date 2022-03-09
 * @since 0.5.0
 */
public interface DSSContextRequestRef<R extends DSSContextRequestRef<R>> extends DevelopmentRequestRef<R> {

    default String getContextId() {
        return (String) getParameter("dssContextId");
    }

    default R setContextId(String contextIdStr) {
        setParameter("dssContextId", contextIdStr);
        return (R) this;
    }

}
