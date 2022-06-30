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

public interface AppStatus {

    /**
     * 返回第三方系统的状态，其中：0 代表健康，为绿色；1 代表繁忙，为黄色；2 代表响应慢，为橙色；3 代表失联，为灰色
     * @return 返回状态
     */
    int getStatus();

    /**
     * 当 {@code getStatus()} 非 0 时，请返回诊断信息
     * @return 诊断信息
     */
    String getUnHealthyMsg();

}
