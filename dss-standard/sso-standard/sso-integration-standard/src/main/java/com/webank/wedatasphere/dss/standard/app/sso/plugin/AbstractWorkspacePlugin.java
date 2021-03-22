/*
 *
 *  * Copyright 2019 WeBank
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.webank.wedatasphere.dss.standard.app.sso.plugin;

import com.webank.wedatasphere.dss.standard.app.sso.builder.DssMsgBuilderOperation.DSSMsg;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by enjoyyin on 2020/9/10.
 */
public abstract class AbstractWorkspacePlugin implements WorkspacePlugin {

    private DssMsgCacheOperation dssMsgCacheOperation;

    public void setDssMsgCacheOperation(DssMsgCacheOperation dssMsgCacheOperation) {
        this.dssMsgCacheOperation = dssMsgCacheOperation;
    }

    @Override
    public String getWorkspaceName(HttpServletRequest req) {
        return dssMsgCacheOperation.getWorkspaceInSession(req);
    }

    @Override
    public List<String> getAllUsers(HttpServletRequest req) {
        DSSMsg dssMsg = dssMsgCacheOperation.getDSSMsgInSession(req);
        return getAllUsers(dssMsg);
    }

    protected abstract List<String> getAllUsers(DSSMsg dssMsg);
}
