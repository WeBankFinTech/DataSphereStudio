package com.webank.wedatasphere.dss.standard.app.development.ref.impl;

import com.webank.wedatasphere.dss.common.label.DSSLabel;
import com.webank.wedatasphere.dss.standard.app.development.ref.DevelopmentRequestRef;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.standard.common.entity.ref.RequestRefImpl;

import java.util.List;

/**
 * @author enjoyyin
 * @date 2022-03-09
 * @since 0.5.0
 */
public class DevelopmentRequestRefImpl<R extends DevelopmentRequestRefImpl<R>> extends RequestRefImpl
    implements DevelopmentRequestRef<R> {

    private Workspace workspace;

    @Override
    public R setName(String name) {
        this.name = name;
        return (R) this;
    }

    @Override
    public R setDSSLabels(List<DSSLabel> dssLabels) {
        this.dssLabels = dssLabels;
        return (R) this;
    }

    @Override
    public R setType(String type) {
        this.type = type;
        return (R) this;
    }

    @Override
    public R setWorkspace(Workspace workspace) {
        this.workspace = workspace;
        return (R) this;
    }

    @Override
    public Workspace getWorkspace() {
        return workspace;
    }
}
