/*
 *
 *  * Copyright 2019 WeBank
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.webank.wedatasphere.dss.workflow.entity;

public enum NodeUiValidateType {
    /**
     * 不校验，None不能和下面的校验类型搭配
     */
    None,
    /**
     * 整数类型
     */
    NumInterval,
    /**
     * 在这几个中
     */
    OFT,
    /**
     * 浮点数
     */
    FloatInterval,
    /**
     * 包含
     */
    Contains,
    /**
     * 正则
     */
    Regex,
    /**
     * 自定义函数..validate_range是函数名,由前台提供
     */
    Function,
    /**
     * 不为空
     */
    Required
}
