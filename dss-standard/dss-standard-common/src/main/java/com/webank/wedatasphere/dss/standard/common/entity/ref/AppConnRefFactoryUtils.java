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

package com.webank.wedatasphere.dss.standard.common.entity.ref;

import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.linkis.common.conf.CommonVars;


public class AppConnRefFactoryUtils {

    public static final String APP_CONN_PACKAGE_HEADER = CommonVars.apply("wds.dss.appconn.package.header", "com.webank.wedatasphere.dss.appconn.").getValue();

    public static <R extends Ref> R newAppConnRef(Class<R> clazz, String appConnName) throws DSSErrorException {
        return RefFactory.INSTANCE.newRef(clazz, Thread.currentThread().getContextClassLoader(), APP_CONN_PACKAGE_HEADER + appConnName);
    }

    public static <R extends Ref> R newAppConnRef(Class<R> clazz, ClassLoader classLoader, String appConnName) throws DSSErrorException {
        return RefFactory.INSTANCE.newRef(clazz, classLoader, APP_CONN_PACKAGE_HEADER + appConnName);
    }

    public static <R extends Ref> R newAppConnRefByPackageName(Class<R> clazz, ClassLoader classLoader, String packageName) throws DSSErrorException {
        return RefFactory.INSTANCE.newRef(clazz, classLoader, packageName);
    }

}
