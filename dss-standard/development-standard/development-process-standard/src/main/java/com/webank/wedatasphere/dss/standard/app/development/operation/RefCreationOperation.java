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

import com.webank.wedatasphere.dss.standard.app.development.ref.DSSJobContentRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.ref.RefJobContentResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;

public interface RefCreationOperation<K extends DSSJobContentRequestRef<K>>
        extends DevelopmentOperation<K, RefJobContentResponseRef> {

    /**
     * 该方法会尝试请求第三方应用工具，在 refProject 下创建一个与 DSS Job 一对一关联的第三方 refJob。
     * requestRef 的具体内容，请参考：{@code DSSJobContentRequestRef}。<br/>
     * 如果创建成功，请返回一个包含了第三方应用工具的 job ID（命名为 refJobId）的 {@code RefJobContentResponseRef}，
     * 以便 DSS 接下来可以使用 refJobId 来管理第三方应用工具对应 refProject 工程下的 refJob。
     * <br>
     * 返回的 refJobId 是其他 DevelopmentOperation 能够操作这个第三方应用工具的 Job 的基础。DSS 在调用其他
     * DevelopmentOperation 时，会将该 refJobId 作为方法参数传入，以便用户能正常找到对应的 refJob 进行相应操作。
     * @param requestRef 包含了 DSSJob 信息的 requestRef
     * @return 包含了第三方应用工具的 job ID（命名为 refJobId）的 RefJobContentResponseRef
     * @throws ExternalOperationFailedException 创建失败时抛出该异常
     */
    RefJobContentResponseRef createRef(K requestRef) throws ExternalOperationFailedException;

}
