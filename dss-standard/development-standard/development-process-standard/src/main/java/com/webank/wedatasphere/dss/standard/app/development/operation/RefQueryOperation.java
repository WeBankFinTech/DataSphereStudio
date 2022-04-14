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


public interface RefQueryOperation<K extends RefJobContentRequestRef<K>, V extends ResponseRef>
        extends DevelopmentOperation<K, V> {

    /**
     * 该方法会尝试请求第三方应用工具，在 refProject 下获取第三方 AppConn 的 refJob 的一些信息。
     * DSS 框架允许用户实现多个 RefQueryOperation，用于查询 refJob 的个性化信息。
     * 目前 DSS 框架层最希望第三方 AppConn 实现的是 {@code RefQueryJumpUrlOperation}。
     * @param requestRef 带上最新的 DSSJob 的所有信息的 requestRef
     * @return 带有第三方应用 refJob 希望返回的信息。
     * @throws ExternalOperationFailedException 查询失败时抛出该异常
     */
    V query(K requestRef) throws ExternalOperationFailedException;

}
