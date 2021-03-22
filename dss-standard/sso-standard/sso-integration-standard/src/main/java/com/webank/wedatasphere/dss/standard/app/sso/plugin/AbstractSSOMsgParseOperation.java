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

import com.webank.wedatasphere.dss.standard.app.sso.builder.SSOBuilderService;
import com.webank.wedatasphere.dss.standard.app.sso.plugin.impl.SSOMsgImpl;
import com.webank.wedatasphere.dss.standard.app.sso.builder.DssMsgBuilderOperation.DSSMsg;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by enjoyyin on 2020/9/3.
 */
public abstract class AbstractSSOMsgParseOperation implements SSOMsgParseOperation {

    protected SSOBuilderService ssoBuilderService;

    public void setSSOBuilderService(SSOBuilderService ssoBuilderService) {
        this.ssoBuilderService = ssoBuilderService;
    }

    @Override
    public boolean isDssRequest(HttpServletRequest request) {
        return ssoBuilderService.createDssMsgBuilderOperation().setParameterMap(request.getParameterMap()).isDSSMsgRequest();
    }

    @Override
    public DSSMsg getDSSMsg(HttpServletRequest request) {
        return ssoBuilderService.createDssMsgBuilderOperation()
            .setParameterMap(request.getParameterMap()).getBuiltMsg();
    }

    @Override
    public SSOMsg getSSOMsg(HttpServletRequest request) {
        DSSMsg dssMsg = getDSSMsg(request);
        SSOMsg ssoMsg = createSSOMsg();
        String user = getUser(dssMsg);
        setSSOMsg(ssoMsg, dssMsg, user);
        return ssoMsg;
    }

    protected SSOMsg createSSOMsg() {
        return new SSOMsgImpl();
    }

    protected void setSSOMsg(SSOMsg ssoMsg, DSSMsg dssMsg, String user) {
        SSOMsgImpl ssoMsgImpl = (SSOMsgImpl) ssoMsg;
        ssoMsgImpl.setRedirectUrl(dssMsg.getRedirectUrl());
        ssoMsgImpl.setWorkspaceName(dssMsg.getWorkspaceName());
        ssoMsgImpl.setUser(user);
    }

    protected abstract String getUser(DSSMsg dssMsg);
}
