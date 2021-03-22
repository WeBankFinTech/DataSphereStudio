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

package com.webank.wedatasphere.dss.standard.common.entity.ref;

import java.lang.reflect.InvocationTargetException;

/**
 * @author allenlliu
 * @date 2020/11/23 15:18
 */
public interface RefFactory<T extends Ref> {


    T newRef(Class<? extends T> clazz) throws IllegalAccessException, InstantiationException;

    /**
     * 我们要将Ref进行实例化,因为各个Ref都是在的不同的AppConn中进行实例化，所以他们的ClassLoader也是和主ClassLoader是不一样的，
     * 所以我们需要将实例化AppConn的ClassLoader传入
     * @param clazz 需要实例化的类继承的接口
     * @param classLoader 实例化appConn的classloader
     * @param packageName 包名
     * @return
     * @throws Exception
     */
    T newRef(Class<? extends T> clazz, ClassLoader classLoader, String packageName) throws Exception;

}