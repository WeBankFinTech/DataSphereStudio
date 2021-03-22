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

package com.webank.wedatasphere.dss.appconn.core;

import com.webank.wedatasphere.dss.standard.app.sso.SSOIntegrationStandard;
import com.webank.wedatasphere.dss.standard.common.core.AppStandard;

import java.util.Collections;
import java.util.List;

/**
 * created by cooperyang on 2020/10/20
 * Description: PresentationAppConn接口是专门用来进行展示信息的第三方系统的连接appconn
 * 例如右侧栏的内容的展示，对于用于展示的内容，它只需要实现SSO规范
 * 还有如DataMap、数据门户、portal等 他们只需要dss上新增一个组件的
 */
public interface PresentationAppConn extends AppConn {

    String getName();

    SSOIntegrationStandard getSSOIntegrationStandard();

    @Override
    default List<AppStandard> getAppStandards() {
        return Collections.singletonList(getSSOIntegrationStandard());
    }

}
