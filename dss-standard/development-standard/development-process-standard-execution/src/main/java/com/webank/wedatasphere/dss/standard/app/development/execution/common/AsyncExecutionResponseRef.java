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

package com.webank.wedatasphere.dss.standard.app.development.execution.common;

import com.webank.wedatasphere.dss.standard.app.development.execution.ExecutionRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.execution.async.RefExecutionResponseListener;
import com.webank.wedatasphere.dss.standard.app.development.execution.core.LongTermRefExecutionOperation;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;

/**
 * Created by enjoyyin on 2021/1/25.
 */
public interface AsyncExecutionResponseRef extends ResponseRef {

    RefExecutionAction getAction();

    void setAction(RefExecutionAction action);

    void addListener(RefExecutionResponseListener listener);

    void setExecutionRequestRef(ExecutionRequestRef requestRef);

    ExecutionRequestRef getExecutionRequestRef();

    LongTermRefExecutionOperation getRefExecution();

    void setRefExecution(LongTermRefExecutionOperation refExecution);

    long getAskStatePeriod();

    void setAskStatePeriod(long askStatePeriod);

    void setCompleted(CompletedExecutionResponseRef response);

    boolean isCompleted();

    long getStartTime();

    long getMaxLoopTime();

    void setMaxLoopTime(long maxLoopTime);

}
