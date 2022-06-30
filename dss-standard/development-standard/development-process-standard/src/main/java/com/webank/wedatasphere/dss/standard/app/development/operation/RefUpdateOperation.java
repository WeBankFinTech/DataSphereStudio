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

import com.webank.wedatasphere.dss.standard.app.development.ref.UpdateRequestRef;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;

public interface RefUpdateOperation<K extends UpdateRequestRef<K>>
        extends DevelopmentOperation<K, ResponseRef>  {

    /**
     * 该方法会尝试请求第三方应用工具，在 refProject 下更新第三方 AppConn 的 refJob。
     * 如果更新成功，请返回一个带有成功标识的 {@code ResponseRef{@code ，例如：ResponseRef.newExternalBuilder().success()；
     * 否则请返回一个带有错误信息的 {@code ResponseRef}，例如：ResponseRef.newExternalBuilder().error("error msg.")
     * 请注意：requestRef 会带上最新的 DSSJob 的所有信息，而不是本次需要更新的信息，各第三方节点可按需更新相关内容。
     * @param requestRef 带上最新的 DSSJob 的所有信息的 requestRef
     * @return 包含了成功或失败标识的 ResponseRef
     * @throws ExternalOperationFailedException 更新失败时抛出该异常
     */
    ResponseRef updateRef(K requestRef) throws ExternalOperationFailedException;

}
