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

package com.webank.wedatasphere.dss.standard.common.desc;
import com.webank.wedatasphere.dss.common.label.DSSLabel;
import com.webank.wedatasphere.dss.standard.common.exception.NoSuchAppInstanceException;

import java.util.List;


public interface AppDesc {

    String getAppName();

    List<AppInstance> getAppInstances();

    /**
     * 对于每个 AppConn 而言，允许拥有多个 AppInstance，如需要获取相应的 AppInstance，
     * 通过传入的 labels 来与 AppInstance 的 getLabels() 所对应的 labels 来进行匹配。<br/>
     * 返回的 List 实例，其中第0个是匹配程度最大的实例。
     * */
    List<AppInstance> getAppInstancesByLabels(List<DSSLabel> labels) throws NoSuchAppInstanceException;

}
