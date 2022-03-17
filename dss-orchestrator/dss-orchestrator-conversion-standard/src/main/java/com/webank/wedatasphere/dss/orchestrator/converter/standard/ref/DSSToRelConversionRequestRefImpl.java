package com.webank.wedatasphere.dss.orchestrator.converter.standard.ref;

import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.standard.common.entity.ref.RequestRefImpl;

/**
 * @author enjoyyin
 * @date 2022-03-16
 * @since 0.5.0
 */
public class DSSToRelConversionRequestRefImpl<R extends DSSToRelConversionRequestRefImpl<R>>
        extends RequestRefImpl implements DSSToRelConversionRequestRef<R> {

    private Workspace workspace;
    private String userName;

    @Override
    public String getUserName() {
        return userName;
    }

    @Override
    public R setUserName(String userName) {
        this.userName = userName;
        return (R) this;
    }

    @Override
    public Workspace getWorkspace() {
        return workspace;
    }

    @Override
    public R setWorkspace(Workspace workspace) {
        this.workspace = workspace;
        return (R) this;
    }

}
