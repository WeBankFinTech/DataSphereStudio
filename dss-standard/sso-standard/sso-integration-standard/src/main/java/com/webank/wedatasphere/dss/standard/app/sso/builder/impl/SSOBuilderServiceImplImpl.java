/*
 * Copyright 2019 WeBank
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.webank.wedatasphere.dss.standard.app.sso.builder.impl;

import com.webank.wedatasphere.dss.standard.app.sso.builder.DssMsgBuilderOperation;
import com.webank.wedatasphere.dss.standard.app.sso.builder.SSOBuilderService;
import com.webank.wedatasphere.dss.standard.app.sso.builder.SSOUrlBuilderOperation;
import com.webank.wedatasphere.dss.standard.common.service.AppServiceImpl;


public class SSOBuilderServiceImplImpl extends AppServiceImpl implements SSOBuilderService {

    private static volatile SSOBuilderService ssoBuilderService;

    public static SSOBuilderService getSSOBuilderService() {
        if(ssoBuilderService == null) {
            synchronized (SSOBuilderServiceImplImpl.class) {
                if(ssoBuilderService == null) {
                    ssoBuilderService = new SSOBuilderServiceImplImpl();
                }
            }
        }
        return ssoBuilderService;
    }

    @Override
    public SSOUrlBuilderOperation createSSOUrlBuilderOperation() {
        return new SSOUrlBuilderOperationImpl();
    }

    @Override
    public DssMsgBuilderOperation createDssMsgBuilderOperation() {
        return new DssMsgBuilderOperationImpl();
    }

}
