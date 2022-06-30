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

import com.webank.wedatasphere.dss.standard.app.development.ref.CopyRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.ref.RefJobContentResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;

public interface RefCopyOperation<K extends CopyRequestRef<K>>
        extends DevelopmentOperation<K, RefJobContentResponseRef> {

    /**
     * 该方法会尝试请求第三方应用工具，在 refProject 下将某个第三方 AppConn 的 refJob 复制为一个全新的 refJob，
     * 关于 requestRef 的定义，请参考 {@code CopyRequestRef}。<br/>
     * 请返回一个在该 refProject 下的全新 refJob。由于该方法一般在工作流新增版本或是复制工作流时使用，所以为了
     * 保持相关节点版本的连续性和可识别性，强烈建议根据 requestRef 提供的 newVersion，为 refJob 命名。
     * 例如：<br/>
     *   requestRef 的 name 为 test，newVersion 为 v2，建议第三方 AppConn 在复制时，新的 refJob 命名为 test_v2。
     * 如果更新成功，请返回一个带有成功标识的 {@code ResponseRef}，例如：ResponseRef.newExternalBuilder().success()；
     * 否则请返回一个带有错误信息的 ResponseRef，例如：ResponseRef.newExternalBuilder().error("error msg.")
     * 请注意：requestRef 会带上最新的 DSSJob 的所有信息，而不是本次需要更新的信息，各第三方节点可按需更新相关内容。
     * @param requestRef 带上最新的 DSSJob 的所有信息的 requestRef
     * @return 包含了成功或失败标识的 ResponseRef
     * @throws ExternalOperationFailedException 复制失败时抛出该异常
     */
    RefJobContentResponseRef copyRef(K requestRef) throws ExternalOperationFailedException;

}
