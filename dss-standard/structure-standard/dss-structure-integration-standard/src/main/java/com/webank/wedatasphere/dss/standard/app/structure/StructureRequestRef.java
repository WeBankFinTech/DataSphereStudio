package com.webank.wedatasphere.dss.standard.app.structure;

import com.webank.wedatasphere.dss.common.label.DSSLabel;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.standard.app.sso.ref.WorkspaceRequestRef;
import com.webank.wedatasphere.dss.standard.common.entity.ref.RequestRefImpl;

import java.util.List;

/**
 * @author enjoyyin
 * @date 2022-03-01
 * @since 0.5.0
 */
public interface StructureRequestRef<R extends StructureRequestRef<R>> extends WorkspaceRequestRef {

    R setName(String name);

    String getUserName();

    R setUserName(String userName);

    R setDSSLabels(List<DSSLabel> dssLabels);

    R setType(String type);

    R setWorkspace(Workspace workspace);

}
