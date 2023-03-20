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

package com.webank.wedatasphere.dss.common.entity.node;

import com.webank.wedatasphere.dss.common.entity.Resource;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface DSSNode extends Node {

    Layout getLayout();

    void setLayout(Layout layout);

    Map<String, Object> getParams();

    void setParams(Map<String, Object> params);

    List<Resource> getResources();

    void setResources(List<Resource> resources);

    Map<String, Object> getJobContent();

    void setJobContent(Map<String, Object> jobContent);

    String getUserProxy();

    void setUserProxy(String userProxy);

    String getModifyUser();
    Long getModifyTime();
    String getDesc();
}
