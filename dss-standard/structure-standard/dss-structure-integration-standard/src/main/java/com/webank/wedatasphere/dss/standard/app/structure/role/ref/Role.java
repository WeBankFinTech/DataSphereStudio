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

package com.webank.wedatasphere.dss.standard.app.structure.role.ref;

import java.util.Date;
import java.util.List;


public interface Role {

    /**
     * 角色英文名，每个工作空间唯一
     * @return 角色英文名
     */
    String getName();

    /**
     * 角色中文名，如果不存在则与 {@code getName()} 完全相同。
     * @return 角色中文名
     */
    String getShowName();

    Date getCreateTime();

    String getCreator();

    String getDescription();

    String getUpdator();

    String getLastUpdateTime();

    /**
     * 该角色目前已绑定的所有用户列表
     * @return
     */
    List<String> getUserNames();

}
