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

package com.webank.wedatasphere.dss.appconn.manager;

import com.webank.wedatasphere.dss.appconn.core.AppConn;
import com.webank.wedatasphere.dss.appconn.core.exception.AppConnErrorException;
import com.webank.wedatasphere.dss.appconn.core.exception.AppConnWarnException;
import com.webank.wedatasphere.dss.appconn.manager.impl.AbstractAppConnManager;
import java.util.List;
import java.util.stream.Collectors;


public interface AppConnManager {

    /**
     * init AppConnManager
     */
    void init();

    /**
     * get all AppConns
     */
    List<AppConn> listAppConns();

    default <T extends AppConn> List<T> listAppConns(Class<T> appConnClass) {
        return listAppConns().stream().filter(appConnClass::isInstance).map(appConn -> (T) appConn).collect(Collectors.toList());
    }

    /**
     * Returns an appconn by its name
     */
    AppConn getAppConn(String appConnName);

    default <T extends AppConn> T getAppConn(Class<T> appConnClass) {
        List<AppConn> appConns = listAppConns().stream().filter(appConnClass::isInstance).collect(Collectors.toList());
        if(appConns.isEmpty()) {
            throw new AppConnWarnException(25344, "Cannot find a AppConn instance for " + appConnClass.getSimpleName());
        } else if(appConns.size() > 1) {
            throw new AppConnWarnException(25344, "More than one AppConn instances is found, list: " + appConns);
        }
        return (T) appConns.get(0);
    }

    void reloadAppConn(String appConnName);

    static void setLazyLoad() {
        AbstractAppConnManager.setLazyLoad();
    }

    static AppConnManager getAppConnManager() {
        return AbstractAppConnManager.getAppConnManager();
    }

}
