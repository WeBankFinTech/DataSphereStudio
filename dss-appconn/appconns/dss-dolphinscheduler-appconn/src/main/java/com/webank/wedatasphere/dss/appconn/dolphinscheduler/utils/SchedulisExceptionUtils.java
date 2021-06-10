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

package com.webank.wedatasphere.dss.appconn.dolphinscheduler.utils;

import com.webank.wedatasphere.linkis.common.exception.ErrorException;

import java.lang.reflect.Constructor;

/**
 * created by cooperyang on 2020/11/16
 * Description: 因为所有的异常都是有errorcode和 errorDesc
 * 所以需要拿到这个constructor就好
 */
public class SchedulisExceptionUtils {

    public static <T extends ErrorException> void dealErrorException(int errorCode, String errorDesc,
        Throwable throwable, Class<T> clazz) throws T {
        T errorException = null;
        try {
            Constructor<T> constructor = clazz.getConstructor(int.class, String.class, Throwable.class);
            errorException = constructor.newInstance(errorCode, errorDesc, throwable);
            errorException.setErrCode(errorCode);
            errorException.setDesc(errorDesc);
        } catch (Exception e) {
            throw new RuntimeException(String.format("failed to instance %s", clazz.getName()), e);
        }
        errorException.initCause(throwable);
        throw errorException;
    }

    public static <T extends ErrorException> void dealErrorException(int errorCode, String errorDesc, Class<T> clazz)
        throws T {
        T errorException = null;
        try {
            Constructor<T> constructor = clazz.getConstructor(int.class, String.class);
            errorException = constructor.newInstance(errorCode, errorDesc);
            errorException.setErrCode(errorCode);
            errorException.setDesc(errorDesc);
        } catch (Exception e) {
            throw new RuntimeException(String.format("failed to instance %s", clazz.getName()), e);
        }
        throw errorException;
    }

}
