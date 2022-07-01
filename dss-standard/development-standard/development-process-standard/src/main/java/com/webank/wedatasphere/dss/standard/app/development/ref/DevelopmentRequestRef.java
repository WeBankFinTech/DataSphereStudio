package com.webank.wedatasphere.dss.standard.app.development.ref;

import com.webank.wedatasphere.dss.common.label.DSSLabel;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.standard.app.sso.ref.WorkspaceRequestRef;

import java.util.List;

/**
 * @author enjoyyin
 * @date 2022-03-09
 * @since 0.5.0
 */
public interface DevelopmentRequestRef<R extends DevelopmentRequestRef<R>> extends WorkspaceRequestRef {

    default R setUserName(String userName) {
        setParameter("userName", userName);
        return (R) this;
    }

    default String getUserName() {
        return (String) getParameter("userName");
    }

    R setName(String name);

    R setDSSLabels(List<DSSLabel> dssLabels);

    R setType(String type);

    R setWorkspace(Workspace workspace);

}
