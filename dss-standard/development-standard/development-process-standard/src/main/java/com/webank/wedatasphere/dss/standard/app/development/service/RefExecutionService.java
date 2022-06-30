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

import com.webank.wedatasphere.dss.standard.app.development.operation.RefExecutionOperation;

/**
 * Job 执行规范，主要用于执行第三方应用工具的 Job。
 * 建议继承 AbstractRefExecutionService。
 */
public interface RefExecutionService extends DevelopmentService {

     /**
      * 执行第三方 AppConn 的 refJob。
      * @return RefExecutionOperation 实现类
      */
     RefExecutionOperation getRefExecutionOperation();

}
