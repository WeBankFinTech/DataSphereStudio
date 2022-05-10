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

package com.webank.wedatasphere.dss.standard.app.structure.role;

import com.webank.wedatasphere.dss.standard.app.structure.StructureOperation;
import com.webank.wedatasphere.dss.standard.app.structure.role.ref.RefRoleContentRequestRef;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;

/**
 * 请求第三方系统删除关联的第三方 refRole。
 * @param <R>
 */
public interface RoleDeletionOperation<R extends RefRoleContentRequestRef<R>>
        extends StructureOperation<R, ResponseRef> {

    /**
     * 请求第三方系统删除关联的第三方 refRole。
     * 删除时，请基于 requestRef.getRefRoleId() 进行删除。
     * @param requestRef 包含了 refRoleId 信息的 RequestRef
     * @return 成功返回 ResponseRef.newExternalBuilder().success() 即可，失败请带上 error 信息
     */
    ResponseRef deleteRole(R requestRef);

}
