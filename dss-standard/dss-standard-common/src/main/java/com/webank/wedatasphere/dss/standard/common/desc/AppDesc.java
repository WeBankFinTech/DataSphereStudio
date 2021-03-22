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

package com.webank.wedatasphere.dss.standard.common.desc;



import com.webank.wedatasphere.dss.common.entity.DSSLabel;
import com.webank.wedatasphere.dss.standard.common.exception.NoSuchAppInstanceException;

import java.util.List;

/**
 * Created by enjoyyin on 2020/9/9.
 */
public interface AppDesc {

    Long getId();

    String getAppName();

    List<AppInstance> getAppInstances();

    /**
     * 对于每个AppConn而言，会拥有多个AppInstance，如需要获取相应的AppInstance，
     * 通过传入的labels来进行匹配。
     * 返回的List实例，其中第0个是匹配程度最大的实例。
     * */
    List<AppInstance> getAppInstancesByLabels(List<DSSLabel> labels) throws NoSuchAppInstanceException;

    void addAppInstance(AppInstance appInstance);

    void removeAppInstance(AppInstance appInstance);

}
