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

package com.webank.wedatasphere.dss.standard.app.structure.status;

import com.webank.wedatasphere.dss.standard.app.structure.StructureService;

/**
 * 第三方应用状态检查规范。
 */
public interface AppStatusService extends StructureService {

    /**
     * 返回第三方应用状态检查操作。
     * @return AppStatusOperation 实现类
     */
    AppStatusOperation getAppStatusOperation();

}
