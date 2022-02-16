package com.webank.wedatasphere.dss.appconn.visualis.ref;

import com.webank.wedatasphere.dss.standard.app.development.ref.CopyRequestRef;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.standard.common.entity.ref.AbstractRequestRef;

public class VisualisCopyRequestRef extends AbstractRequestRef implements CopyRequestRef {

    private Workspace workspace;

    @Override
    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
    }

    @Override
    public Workspace getWorkspace() {
        return this.workspace;
    }
}
