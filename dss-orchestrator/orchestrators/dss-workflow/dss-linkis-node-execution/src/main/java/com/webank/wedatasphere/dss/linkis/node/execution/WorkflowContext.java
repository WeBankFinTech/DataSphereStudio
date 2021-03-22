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

package com.webank.wedatasphere.dss.linkis.node.execution;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by enjoyyin on 2019/10/29.
 */
public interface WorkflowContext {

    Object getValue(String key);

    List<Object> getValues(String prefix);

    Iterator<String> keyIterator();

    Map<String, Object> getSubMapByPrefix(String keyPrefix);


    void setValue(String key, Object value, int readTimes);

    static WorkflowContext getAppJointContext() {
        return WorkflowContextFactory.getWorkflowContext();
    }

}