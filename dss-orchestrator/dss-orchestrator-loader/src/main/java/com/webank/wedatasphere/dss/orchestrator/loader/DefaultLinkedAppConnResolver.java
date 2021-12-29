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
import com.webank.wedatasphere.dss.appconn.manager.AppConnManager;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;



@Component
class DefaultLinkedAppConnResolver implements LinkedAppConnResolver  {


    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultLinkedAppConnResolver.class);


    static{
        LOGGER.info("component resolver inited");
    }

    @Override
    public List<AppConn> resolveAppConnByUser(String userName, String workspaceName, String typeName) {
        //todo 后面可以使用数据库表来定义用户可以加载的AppConn.
        List<AppConn> appConns =  new ArrayList<>();
        for(AppConn appConn : AppConnManager.getAppConnManager().listAppConns()){
            //可以在这里根据用户情况和工作空间情况，限制appConn的加载
            appConns.add(appConn);
        }

        return appConns;
    }
}
