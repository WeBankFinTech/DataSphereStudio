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

package com.webank.wedatasphere.dss.standard.app.development.operation;

import com.webank.wedatasphere.dss.standard.app.development.ref.RefJobContentRequestRef;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;

public interface RefDeletionOperation<K extends RefJobContentRequestRef<K>> extends DevelopmentOperation<K, ResponseRef> {

    /**
     * 该方法会尝试请求第三方应用工具，在 refProject 下删除第三方 AppConn 的 refJob。
     * 如果删除成功，请返回一个带有成功标识的 {@code ResponseRef}，例如：ResponseRef.newExternalBuilder().success()；
     * 否则请返回一个带有错误信息的 ResponseRef，例如：ResponseRef.newExternalBuilder().error("error msg.")
     * @param requestRef 包含了第三方 refJob 信息的 requestRef
     * @return 包含了成功或失败标识的 ResponseRef
     * @throws ExternalOperationFailedException 删除失败时抛出该异常
     */
    ResponseRef deleteRef(K requestRef) throws ExternalOperationFailedException;

}
