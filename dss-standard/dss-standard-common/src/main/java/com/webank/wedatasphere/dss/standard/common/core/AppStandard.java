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

package com.webank.wedatasphere.dss.standard.common.core;



import com.webank.wedatasphere.dss.standard.common.desc.AppDesc;
import com.webank.wedatasphere.dss.standard.common.exception.AppStandardErrorException;

import java.io.Closeable;

/**
 * Created by enjoyyin on 2020/9/1.
 */
public interface AppStandard extends Closeable {

    AppDesc getAppDesc();

    void setAppDesc(AppDesc appDesc);

    String getStandardName();

    int getGrade();

    void init() throws AppStandardErrorException;

    /**
     *  是否必须
     * @return true 表示必须，false表示非必须标准
     */
    default boolean isNecessary() {
        return false;
    }

}
