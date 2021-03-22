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

package com.webank.wedatasphere.dss.standard.app.development.execution.async;

import com.webank.wedatasphere.dss.standard.app.development.execution.ExecutionRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.execution.common.AsyncExecutionResponseRef;
import com.webank.wedatasphere.dss.standard.app.development.execution.common.CompletedExecutionResponseRef;
import com.webank.wedatasphere.dss.standard.app.development.execution.common.RefExecutionAction;

/**
 * Created by enjoyyin on 2021/1/22.
 */
public interface RefExecutionStatusListener extends RefExecutionListener {

    void beforeSubmit(ExecutionRequestRef requestRef);

    void afterSubmit(ExecutionRequestRef requestRef, RefExecutionAction action);

    void afterAsyncResponseRef(AsyncExecutionResponseRef response);

    void afterCompletedExecutionResponseRef(ExecutionRequestRef requestRef, RefExecutionAction action,
        CompletedExecutionResponseRef response);

}
