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

package com.webank.wedatasphere.dss.framework.appconn.service;

import com.webank.wedatasphere.dss.appconn.core.AppConn;
import com.webank.wedatasphere.dss.appconn.core.PresentationAppConn;
import com.webank.wedatasphere.dss.appconn.core.exception.AppConnErrorException;
import com.webank.wedatasphere.dss.standard.common.desc.AppInstance;

import java.util.List;

/**
 * created by cooperyang on 2020/11/18
 * Description:
 */
public interface AppConnService {


    /**
     * 通过该接口可以热刷新AppConn
     * */
    void refreshAppConns();

    /**
     * 获取所有的AppConns
     * @return 返回所有注册的AppConn
     */

    List<AppConn> listAppConns();


    /**
     * 通过的AppConn的名字返回一个AppConn
     * @param appConnName appconn的名字
     * @return 符合的AppConn
     * @throws AppConnErrorException
     */
    AppConn getAppConn(String appConnName);

    /**
     * 通过nodeType获取到AppConn
     * @param nodeType
     * @return
     */
    AppConn getAppConnByNodeType(String nodeType);

    String getRedirectUrl(String nodeType, AppInstance appInstance);

    /**
     * 展示所有的展示类第三方系统的AppConn,包括如知识库、论坛等的第三方应用
     * @return 返回第三方系统的appconn
     */
    List<PresentationAppConn> getPresentationAppConns();


    /**
     * 通过clazz来获取到一个可用的AppConn
     * @param clazz
     * @return
     */
    AppConn getAppConn(Class<? extends AppConn> clazz);


}
