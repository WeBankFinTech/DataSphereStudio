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

package com.webank.wedatasphere.dss.orchestrator.loader;

import com.webank.wedatasphere.dss.appconn.core.AppConn;


import java.util.List;

public interface LinkedAppConnResolver {
    /**
     * 根据用户、工作空间、appconn的类型，获取所有注册过的符合条件的appconn
     * @param userName 用户名
     * @param workspaceName 工作空间名
     * @param typeName appconn类型
     * @return 注册过的符合条件的appconn
     */
    List<AppConn>   resolveAppConnByUser(String userName, String workspaceName, String typeName);
}
