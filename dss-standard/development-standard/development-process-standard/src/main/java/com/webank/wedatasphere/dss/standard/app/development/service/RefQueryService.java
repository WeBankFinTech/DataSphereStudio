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

package com.webank.wedatasphere.dss.standard.app.development.service;

import com.webank.wedatasphere.dss.standard.app.development.operation.RefQueryJumpUrlOperation;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefQueryOperation;

/**
 * Job 查询规范，主要用于打开第三方应用工具的 Job 的页面。
 */
public interface RefQueryService extends DevelopmentService {

    /**
     * 用于当用户在前端双击该工作流节点时，返回一个可以跳转的 jumpURL
     * @return RefQueryJumpUrlOperation 实现类
     */
    RefQueryJumpUrlOperation getRefQueryOperation();

    /**
     * 在 refProject 下获取第三方 AppConn 的 refJob 的一些信息。
     * DSS 框架允许用户实现多个 RefQueryOperation，用于查询 refJob 的个性化信息。
     * @param clazz RefQueryOperation 实现类的 class 对象
     * @return clazz 所代表的实例
     */
    RefQueryOperation getExtraRefQueryOperation(Class<RefQueryOperation> clazz);

}
