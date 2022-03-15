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


public interface RefExecutionRequestRef<R extends RefExecutionRequestRef<R>>
        extends RefJobContentRequestRef<R> {

    default ExecutionRequestRefContext getExecutionRequestRefContext() {
        return (ExecutionRequestRefContext) getParameter("executionRequestRefContext");
    }

    default R setExecutionRequestRefContext(ExecutionRequestRefContext executionRequestRefContext) {
        setParameter("executionRequestRefContext", executionRequestRefContext);
        return (R) this;
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
