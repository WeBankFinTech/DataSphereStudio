/*
 * Copyright 2019 WeBank
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
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


import java.util.List;

/**
 * Created by enjoyyin on 2019/5/14.
 */
public interface Node {
    String getId();

    void setId(String id);

    String getNodeType();

    void setNodeType(String nodeType);

    String getName();

    void setName(String name);

    void addDependency(String nodeName);

    void setDependency(List<String> dependency);

    void removeDependency(String nodeName);

    List<String> getDependencys();
}
