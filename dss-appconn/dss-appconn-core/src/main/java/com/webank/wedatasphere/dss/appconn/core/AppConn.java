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

import com.webank.wedatasphere.dss.appconn.core.impl.AppConnImpl;
import com.webank.wedatasphere.dss.standard.common.core.AppStandard;
import com.webank.wedatasphere.dss.standard.common.desc.AppDesc;

import java.util.List;

/**
 * Created by enjoyyin on 2020/9/9.
 */
public interface AppConn {

    /**
     * 1.拿到 dssappconnbean 表记录
     * 2. 做一个遍历，拿到每一个appconn 下的所有appinstance
     * 3.实例化真正的appConn接口
     */

    List<AppStandard> getAppStandards();

    AppDesc getAppDesc();

     default void setAppDesc(AppDesc appDesc){

     }

    static AppConnBuilder newBuilder() {
        return AppConnImpl.newBuilder();
    }

    interface AppConnBuilder {

        AppConnBuilder setAppDesc(AppDesc appDesc);

        AppConnBuilder setAppStandards(List<AppStandard> appStandards);

        AppConnBuilder addAppStandard(AppStandard appStandard);

        AppConn build();

    }

}
