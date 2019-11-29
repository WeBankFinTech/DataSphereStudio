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

package com.webank.wedatasphere.dss.linkis.node.execution;

import com.webank.wedatasphere.dss.linkis.node.execution.conf.LinkisJobExecutionConfiguration;
import com.webank.wedatasphere.dss.linkis.node.execution.exception.LinkisJobExecutionWarnException;
import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;

/**
 * Created by enjoyyin on 2019/10/29.
 */
public class WorkflowContextFactory {

    private static Class<? extends WorkflowContext> clazz = WorkflowContextImpl.class;
    private static WorkflowContext workflowContext = null;

    public static void register(Class<? extends WorkflowContext> clazz) {
        WorkflowContextFactory.clazz = clazz;
    }

    static WorkflowContext getWorkflowContext() {
        if(workflowContext == null) {
            synchronized (WorkflowContextFactory.class) {
                if(workflowContext == null) {
                    String className = LinkisJobExecutionConfiguration.APPJOINT_CONTEXT_CLASS.acquireNew();
                    if(clazz == WorkflowContextImpl.class && StringUtils.isNotBlank(className)) {
                        try {
                            clazz = ClassUtils.getClass(className);
                        } catch (ClassNotFoundException e) {
                            throw new LinkisJobExecutionWarnException(90150, "find class + " + className + " failed!", e);
                        }
                    }
                    try {
                        workflowContext = clazz.newInstance();
                    } catch (Exception e) {
                        throw new LinkisJobExecutionWarnException(90150, "initial class + " + className + " failed!", e);
                    }
                }
            }
        }
        return workflowContext;
    }
}
