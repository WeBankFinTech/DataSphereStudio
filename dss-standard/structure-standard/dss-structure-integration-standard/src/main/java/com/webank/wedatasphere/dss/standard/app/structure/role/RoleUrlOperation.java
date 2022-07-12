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
import com.webank.wedatasphere.dss.standard.app.structure.role.ref.RoleUrlResponseRef;

/**
 * 用于当用户在 DSS 前端设置第三方系统的这个角色的权限信息时，返回第三方系统该角色的前端跳转页面 jumpURL。
 * 该功能主要是为了尽量避免用户需要跳转多个系统去设置角色权限所预留的。
 * <br/>
 * 设计的初衷是尽量不干扰第三方系统的角色权限体系，只是通过 Iframe 嵌入的方式，方便用户在一个页面即可完成所有角色
 * 权限信息的设置。
 */
public interface RoleUrlOperation<R extends RefRoleContentRequestRef<R>>
        extends StructureOperation<R, RoleUrlResponseRef> {

    /**
     * 用于当用户在 DSS 前端设置第三方系统的这个角色的权限信息时，返回第三方系统该角色的前端跳转页面 jumpURL。
     * @param requestRef 带有 refRoleId 信息的 RefRoleContentRequestRef
     * @return 请返回带有 jumpURL 的 RoleUrlResponseRef
     */
    RoleUrlResponseRef getRoleUrl(R requestRef);

}
