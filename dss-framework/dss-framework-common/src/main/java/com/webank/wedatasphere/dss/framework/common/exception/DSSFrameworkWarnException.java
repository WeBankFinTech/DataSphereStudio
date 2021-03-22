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

package com.webank.wedatasphere.dss.framework.common.exception;

import com.webank.wedatasphere.linkis.common.exception.WarnException;

/**
 * created by cooperyang on 2020/9/28
 * Description:
 */
public class DSSFrameworkWarnException extends WarnException {
    public DSSFrameworkWarnException(int errorCode, String errorDesc){
        super(errorCode, errorDesc);
    }

    public static void dealWarnException(int errorCode, String errorDesc, Throwable t) throws DSSFrameworkWarnException {
        DSSFrameworkWarnException dssFrameworkWarnException = new DSSFrameworkWarnException(errorCode, errorDesc);
        dssFrameworkWarnException.initCause(t);
        throw dssFrameworkWarnException;
    }

}
