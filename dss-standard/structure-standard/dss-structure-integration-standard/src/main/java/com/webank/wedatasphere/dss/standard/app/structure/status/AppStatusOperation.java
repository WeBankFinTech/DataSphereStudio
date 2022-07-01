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

package com.webank.wedatasphere.dss.standard.app.structure.status;

import com.webank.wedatasphere.dss.standard.app.structure.StructureOperation;
import com.webank.wedatasphere.dss.standard.common.entity.ref.RequestRef;

/**
 * 检查第三方系统的状态，如果发现该第三方系统出现异常，以便及时告警或展示给到前端使用用户。
 */
public interface AppStatusOperation
        extends StructureOperation<RequestRef, AppStatusResponseRef> {

    /**
     * 无入参，仅用于检查第三方系统的状态。
     * @return 返回心跳结果
     */
    AppStatusResponseRef heartbeat();

}
