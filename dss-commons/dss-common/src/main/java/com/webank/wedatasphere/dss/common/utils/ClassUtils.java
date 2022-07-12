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

package com.webank.wedatasphere.dss.common.utils;

import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.common.exception.DSSRuntimeException;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.reflections.Reflections;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ClassUtils {

    private static final ClassHelper CLASS_HELPER = new ClassHelper() {
        @Override
        protected Reflections getReflections(Class<?> clazz) {
            return org.apache.linkis.common.utils.ClassUtils.reflections();
        }
    };

    public static <T> T getInstance(Class<T> clazz) throws DSSErrorException {
        return CLASS_HELPER.getInstance(clazz);
    }

    public static <T> T getInstanceOrWarn(Class<T> clazz) {
        return CLASS_HELPER.getInstanceOrWarn(clazz);
    }

    public static <T> T getInstanceOrDefault(Class<T> clazz, T defaultValue) {
        return CLASS_HELPER.getInstanceOrDefault(clazz, defaultValue);
    }

    public static <T> T getInstanceOrDefault(Class<T> clazz, Predicate<Class<? extends T>> filterOp, T defaultValue) {
        return CLASS_HELPER.getInstanceOrDefault(clazz, filterOp, defaultValue);
    }

    public static <T> List<T> getInstances(Class<T> clazz) {
        return CLASS_HELPER.getInstances(clazz);
    }

    public static <T> List<T> getInstances(Class<T> clazz, Predicate<Class<? extends T>> filterOp) {
        return CLASS_HELPER.getInstances(clazz, filterOp);
    }

    public static <T> List<Class<? extends T>> getClasses(Class<T> clazz) {
        return CLASS_HELPER.getClasses(clazz);
    }

    public abstract static class ClassHelper {

        protected abstract Reflections getReflections(Class<?> clazz);

        public <T> T getInstance(Class<T> clazz) throws DSSErrorException {
            List<Class<? extends T>> factoryClasses = getReflections(clazz).getSubTypesOf(clazz)
                .stream().filter(c -> !org.apache.linkis.common.utils.ClassUtils.isInterfaceOrAbstract(c)).collect(Collectors.toList());
            if(factoryClasses.isEmpty()) {
                DSSExceptionUtils.dealErrorException(60053, "Cannot find instance for " + clazz.getSimpleName(), DSSErrorException.class);
            } else if(factoryClasses.size() > 1) {
                DSSExceptionUtils.dealErrorException(60053, "Too many instances for " + clazz.getSimpleName() + ", exists: " + factoryClasses,
                    DSSErrorException.class);
            }
            T t = null;
            try {
                t = factoryClasses.get(0).newInstance();
            } catch (Exception e) {
                DSSExceptionUtils.dealErrorException(60053, "Instance " + clazz.getSimpleName() + " failed", e, DSSErrorException.class);
            }
            return t;
        }

        public <T> T getInstanceOrWarn(Class<T> clazz) {
            try {
                return getInstance(clazz);
            } catch (DSSErrorException e) {
                DSSExceptionUtils.dealWarnException(60053, e.getDesc(), ExceptionUtils.getCause(e), DSSRuntimeException.class);
            }
            return null;
        }

        public <T> T getInstanceOrDefault(Class<T> clazz, T defaultValue) {
            Optional<T> optional = getReflections(clazz).getSubTypesOf(clazz)
                .stream().filter(c -> !org.apache.linkis.common.utils.ClassUtils.isInterfaceOrAbstract(c) &&
                    !c.isInstance(defaultValue)).findFirst().map(DSSExceptionUtils.map(Class::newInstance));
            return optional.orElse(defaultValue);
        }

        public <T> T getInstanceOrDefault(Class<T> clazz, Predicate<Class<? extends T>> filterOp, T defaultValue) {
            Optional<T> optional = getReflections(clazz).getSubTypesOf(clazz)
                .stream().filter(c -> !org.apache.linkis.common.utils.ClassUtils.isInterfaceOrAbstract(c) &&
                    filterOp.test(c)).findFirst().map(DSSExceptionUtils.map(Class::newInstance));
            return optional.orElse(defaultValue);
        }

        public <T> List<T> getInstances(Class<T> clazz) {
            return getInstances(clazz, c -> true);
        }

        public <T> List<T> getInstances(Class<T> clazz, Predicate<Class<? extends T>> filterOp) {
            return getReflections(clazz).getSubTypesOf(clazz)
                    .stream().filter(c -> !org.apache.linkis.common.utils.ClassUtils.isInterfaceOrAbstract(c) && filterOp.test(c))
                    .map(DSSExceptionUtils.map(Class::newInstance)).collect(Collectors.toList());
        }

        public <T> List<Class<? extends T>> getClasses(Class<T> clazz) {
            return getReflections(clazz).getSubTypesOf(clazz)
                .stream().filter(c -> !org.apache.linkis.common.utils.ClassUtils.isInterfaceOrAbstract(c))
                .collect(Collectors.toList());
        }

    }

}
