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
import com.webank.wedatasphere.dss.standard.app.structure.role.ref.DSSRoleContentRequestRef;
import com.webank.wedatasphere.dss.standard.app.structure.role.ref.RoleResponseRef;

/**
 * 请求第三方系统创建一个与 DSS 角色一对一关联的第三方 refRole。
 */
public interface RoleCreationOperation<R extends DSSRoleContentRequestRef<R>>
        extends StructureOperation<R, RoleResponseRef> {

    /**
     * 请求第三方系统创建一个与 DSS 角色一对一关联的第三方 refRole。
     * 考虑到不同的工作空间，其角色名有可能重复，因此建议第三方系统最好将角色名命名为 ${workspaceName}-${roleName}。
     * @param requestRef 包含了 DSS {@code Role} 信息的 RequestRef
     * @return 包含了第三方系统的 Role Id 的 responseRef
     */
    RoleResponseRef createRole(R requestRef);

}
