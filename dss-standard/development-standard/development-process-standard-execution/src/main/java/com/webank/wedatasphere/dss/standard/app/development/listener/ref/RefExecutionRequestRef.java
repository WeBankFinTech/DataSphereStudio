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

package com.webank.wedatasphere.dss.standard.app.development.listener.ref;

import com.webank.wedatasphere.dss.standard.app.development.listener.core.ExecutionRequestRefContext;
import com.webank.wedatasphere.dss.standard.app.development.ref.DSSContextRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.ref.ProjectRefRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.ref.RefJobContentRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.ref.impl.DevelopmentRequestRefImpl;

import java.util.Map;


public interface RefExecutionRequestRef<R extends RefExecutionRequestRef<R>>
        extends RefJobContentRequestRef<R> {

    /**
     * 节点执行的上下文信息类，详见 {@code ExecutionRequestRefContext}。
     * @return 节点执行的上下文信息类
     */
    default ExecutionRequestRefContext getExecutionRequestRefContext() {
        return (ExecutionRequestRefContext) getParameter("executionRequestRefContext");
    }

    default R setExecutionRequestRefContext(ExecutionRequestRefContext executionRequestRefContext) {
        setParameter("executionRequestRefContext", executionRequestRefContext);
        return (R) this;
    }

    /**
     * DSS 工作流设置的所有全局变量
     * 由于第三方 AppConn 的 Job 也可能支持添加自定义变量，我们建议自定义变量的作用域如下：
     *  1. 如果 用户同时在 DSS 工作流和 第三方 AppConn 的 Job 都设置了同一个自定义变量，则以第三方 AppConn 的 Job 的为准
     *  2. 否则使用 DSS 工作流的自定义变量
     * @return 返回 DSS 工作流所设置的所有全局变量
     */
    default Map<String, Object> getVariables() {
        return (Map<String, Object>) getParameter("variables");
    }

    default R setVariables(Map<String, Object> variables) {
        setParameter("variables", variables);
        return (R) this;
    }

    /**
     * 这是一个非常重要的属性，当第三方 AppConn 支持按照选定的日期进行跑批时，那么在执行对应的 refJob 时，请一定要带上该
     * 参数到第三方 refJob，以便一些调度系统想要做历史跑批时，可以正确地执行其对应日期的作业。
     * @return null if not exists, otherwise return the date string, the date format is yyyyMMdd.
     */
    default String getRunDate() {
        if(getVariables().containsKey("run_date")) {
            return (String) getVariables().get("run_date");
        } else {
            return null;
        }
    }

    /**
     * If the third-part AppConn has no project, and don't wants to integrate with DSS context service,
     * please use this class to asyncly execute refJob.
     */
    class RefExecutionRequestRefImpl extends DevelopmentRequestRefImpl<RefExecutionRequestRefImpl>
        implements RefExecutionRequestRef<RefExecutionRequestRefImpl> {}

    /**
     * If the third-part AppConn has project, and don't wants to integrate with DSS context service,
     * please use this class to asyncly execute refJob.
     */
    class RefExecutionProjectRequestRef extends DevelopmentRequestRefImpl<RefExecutionProjectRequestRef>
            implements RefExecutionRequestRef<RefExecutionProjectRequestRef>,
            ProjectRefRequestRef<RefExecutionProjectRequestRef> {}

    /**
     * If the third-part AppConn wants to integrate with DSS context service,
     * please use this class to asyncly execute refJob.
     */
    class RefExecutionContextRequestRef extends DevelopmentRequestRefImpl<RefExecutionContextRequestRef>
            implements RefExecutionRequestRef<RefExecutionContextRequestRef>,
            DSSContextRequestRef<RefExecutionContextRequestRef> {}

    /**
     * If the third-part AppConn has project, and wants to integrate with DSS context service,
     * please use this class to asyncly execute refJob.
     */
    class RefExecutionProjectWithContextRequestRef extends DevelopmentRequestRefImpl<RefExecutionProjectWithContextRequestRef>
            implements RefExecutionRequestRef<RefExecutionProjectWithContextRequestRef>,
            ProjectRefRequestRef<RefExecutionProjectWithContextRequestRef>,
            DSSContextRequestRef<RefExecutionProjectWithContextRequestRef> {}

}
