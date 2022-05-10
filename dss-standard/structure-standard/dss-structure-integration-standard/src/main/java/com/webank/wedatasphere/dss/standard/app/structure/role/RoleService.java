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

import com.webank.wedatasphere.dss.standard.app.structure.StructureService;

/**
 * 统一角色规范，用于打通DSS与各集成接入系统的角色体系。
 * 该规范为预留规范，DSS 框架层暂未与这两个规范进行对接。
 */
public interface RoleService extends StructureService {

    /**
     * 请求第三方系统创建一个与 DSS 角色一对一关联的第三方 refRole。
     * @return RoleCreationOperation 实现类
     */
    RoleCreationOperation getRoleCreationOperation();

    /**
     * 请求第三方系统更新与 DSS 角色已经一对一关联的第三方 refRole。
     * @return RoleUpdateOperation 实现类
     */
    RoleUpdateOperation getRoleUpdateOperation();

    /**
     * 请求第三方系统删除关联的第三方 refRole。
     * @return RoleDeletionOperation 实现类
     */
    RoleDeletionOperation getRoleDeletionOperation();

    /**
     * 用于当用户在 DSS 前端设置第三方系统的这个角色的权限信息时，返回第三方系统该角色的前端跳转页面 jumpURL。
     * @return RoleUrlOperation 实现类
     */
    RoleUrlOperation getRoleUrlOperation();

}
