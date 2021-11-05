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


public interface RefFactory {

    RefFactory INSTANCE = new DefaultRefFactory();

    <R extends Ref> R newRef(Class<R> clazz) throws DSSErrorException;

    /**
     * We need to instantiate Ref, because each Ref is instantiated in a different AppConn,
     * so their ClassLoader is also different from the main ClassLoader,
     * so we need to pass in the ClassLoader that instantiates the AppConn.
     * @param clazz The interface inherited by the class that needs to be instantiated.
     * @param classLoader Instantiate the classloader of appConn.
     * @param packageName package name.
     * @return return a instance of R.
     * @throws DSSErrorException
     */
    <R extends Ref> R newRef(Class<R> clazz, ClassLoader classLoader, String packageName) throws DSSErrorException;

}