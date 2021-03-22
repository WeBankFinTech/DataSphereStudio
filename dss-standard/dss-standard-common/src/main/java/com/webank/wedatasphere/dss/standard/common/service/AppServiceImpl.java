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

package com.webank.wedatasphere.dss.standard.common.service;

import com.webank.wedatasphere.dss.standard.common.exception.AppStandardWarnException;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by enjoyyin on 2020/9/2.
 */
public class AppServiceImpl implements AppService {

    private Set<Class<? extends Operation>> necessaryOperations = new HashSet<>();

    protected void registerNecessaryOperation(Class<? extends Operation> clazz) {
        necessaryOperations.add(clazz);
    }

    @Override
    public Operation createOperation(Class<? extends Operation> clazz) {
        String clazzSimpleName = clazz.getSimpleName();
        List<Method> methodList = Arrays.stream(this.getClass().getDeclaredMethods())
            .filter(method -> method.getReturnType() == clazz
            && method.getParameterCount() == 0).collect(Collectors.toList());
        if(methodList.size() == 1) {
            try {
                return (Operation) methodList.get(0).invoke(this);
            } catch (ReflectiveOperationException e) {
                throw new AppStandardWarnException(80020, "Not exists operation: " + clazzSimpleName, e);
            }
        } else if(methodList.isEmpty()) {
            return notFoundOperation(clazz);
        } else {
            return multiFoundOperation(clazz);
        }
    }

    protected Operation notFoundOperation(Class<? extends Operation> clazz) {
        throw new AppStandardWarnException(80020, "Not exists operation: " + clazz.getSimpleName());
    }

    protected Operation multiFoundOperation(Class<? extends Operation> clazz) {
        throw new AppStandardWarnException(80020, "Multi exists operations: " + clazz.getSimpleName());
    }

    @Override
    public boolean isOperationExists(Class<? extends Operation> clazz) {
        try{
            return createOperation(clazz) != null;
        } catch (AppStandardWarnException e) {
            return false;
        }
    }

    @Override
    public boolean isOperationNecessary(Class<? extends Operation> clazz) {
        boolean isNecessary = necessaryOperations.contains(clazz);
        if(isNecessary) {
            return true;
        } else {
            return isOperationExists(clazz);
        }
    }
}
