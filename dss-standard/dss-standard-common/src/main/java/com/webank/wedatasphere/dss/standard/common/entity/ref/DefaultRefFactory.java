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

import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.common.utils.DSSExceptionUtils;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class DefaultRefFactory<T extends Ref> implements RefFactory<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultRefFactory.class);

    private class RefKey{
        private ClassLoader classLoader;
        private Class<? extends T> clazz;
        private String packageName;

        public RefKey(ClassLoader classLoader, Class<? extends T> clazz, String packageName) {
            this.classLoader = classLoader;
            this.clazz = clazz;
            this.packageName = packageName;
        }

        @SuppressWarnings("unchecked")
        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            RefKey refKey = (RefKey) o;
            return com.google.common.base.Objects.equal(classLoader, refKey.classLoader) &&
                    com.google.common.base.Objects.equal(clazz, refKey.clazz) &&
                    com.google.common.base.Objects.equal(packageName, refKey.packageName);
        }

        @Override
        public int hashCode() {
            return com.google.common.base.Objects.hashCode(classLoader, clazz, packageName);
        }
    }



    private Map<RefKey, Class<? extends T>> cacheMap = new ConcurrentHashMap<>(16);


    @Override
    public T newRef(Class<? extends T> clazz) throws IllegalAccessException, InstantiationException {
        return clazz.newInstance();
    }

    @Override
    public T newRef(Class<? extends T> clazz, ClassLoader classLoader, String packageName) throws Exception {
        RefKey refKey = new RefKey(classLoader, clazz, packageName);
        if(cacheMap.containsKey(refKey)){
            return cacheMap.get(refKey).newInstance();
        }
        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
        configurationBuilder.addClassLoader(classLoader);
        configurationBuilder.addClassLoader(clazz.getClassLoader());
        configurationBuilder.forPackages(packageName);
        Collection<URL> urls = ClasspathHelper.forClassLoader(classLoader);
        configurationBuilder.addUrls(urls.stream().filter(Objects::nonNull).collect(Collectors.toList()));
        configurationBuilder.addScanners(new SubTypesScanner());
        Reflections reflections = new Reflections(configurationBuilder);
        Set<? extends Class<? extends T>> subClasses = reflections.getSubTypesOf(clazz);
        if(subClasses.isEmpty()){
            return newRef(clazz);
        } else {
            Set<? extends Class<? extends T>> realSubClasses = null;
            if (subClasses.size() > 1){
                LOGGER.warn("subClass of {} size is {}, classes are {}", clazz.getName(), subClasses.size(), subClasses);
                realSubClasses = subClasses.stream().filter(subClass -> subClass.getName().contains(packageName) && !((Class) subClass).isInterface()).collect(Collectors.toSet());
                LOGGER.warn("realSubClasses is {} ", realSubClasses);
            }else{
                realSubClasses = subClasses;
            }
            if (realSubClasses.size() > 1){
                LOGGER.error("realSubClasses size is bigger than 1");
                DSSExceptionUtils.dealErrorException(60091, "too many subclass of " + clazz.getName() + "in " + packageName,
                        DSSErrorException.class);
            }
            Class<? extends T> subClass = realSubClasses.iterator().next();
            cacheMap.put(refKey, subClass);
            return subClass.newInstance();
        }
    }

}
