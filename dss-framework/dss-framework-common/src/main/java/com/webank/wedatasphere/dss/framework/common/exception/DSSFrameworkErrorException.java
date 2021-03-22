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

import com.webank.wedatasphere.linkis.common.exception.ErrorException;

/**
 * created by cooperyang on 2020/9/28
 * Description: DSS框架的错误码,从71000到72000
 */
public class DSSFrameworkErrorException extends ErrorException {

    public DSSFrameworkErrorException(int errorCode, String errorDesc){
        super(errorCode, errorDesc);
    }

    public static void dealErrorException(int errorCode, String errorDesc, Throwable t) throws DSSFrameworkErrorException{
        DSSFrameworkErrorException dssFrameworkErrorException = new DSSFrameworkErrorException(errorCode, errorDesc);
        dssFrameworkErrorException.initCause(t);
        throw dssFrameworkErrorException;
    }

    public static void dealErrorException(int errorCode, String errorDesc) throws DSSFrameworkErrorException{
        throw new DSSFrameworkErrorException(errorCode, errorDesc);
    }


}
