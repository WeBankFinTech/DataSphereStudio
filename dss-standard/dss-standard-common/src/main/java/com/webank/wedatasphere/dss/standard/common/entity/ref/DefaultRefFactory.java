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
import com.webank.wedatasphere.dss.common.utils.DSSExceptionUtils;
import com.webank.wedatasphere.linkis.common.utils.ClassUtils;
import java.net.URL;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultRefFactory implements RefFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultRefFactory.class);

    private class RefKey{
        private ClassLoader classLoader;
        private Class<? extends Ref> clazz;
        private String packageName;

        public RefKey(ClassLoader classLoader, Class<? extends Ref> clazz, String packageName) {
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



    private Map<RefKey, Class<? extends Ref>> cacheMap = new ConcurrentHashMap<>(16);


    @Override
    public <R extends Ref> R newRef(Class<R> clazz) throws DSSErrorException {
        if(!ClassUtils.isInterfaceOrAbstract(clazz)) {
            return DSSExceptionUtils.tryAndWarn(Void -> clazz.newInstance());
        } else {
            return com.webank.wedatasphere.dss.common.utils.ClassUtils.getInstance(clazz);
        }
    }

    @Override
    public <R extends Ref> R newRef(Class<R> clazz, ClassLoader classLoader, String packageName) throws DSSErrorException {
        RefKey refKey = new RefKey(classLoader, clazz, packageName);
        Class<? extends Ref> refClass = cacheMap.get(refKey);
        if(cacheMap.containsKey(refKey) && refClass != null){
            return DSSExceptionUtils.tryAndWarn(Void -> (R) refClass.newInstance());
        } else if(cacheMap.containsKey(refKey) && refClass == null)  {
            return newRef(clazz);
        }
        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
        configurationBuilder.addClassLoader(classLoader);
        configurationBuilder.addClassLoader(clazz.getClassLoader());
        configurationBuilder.forPackages(packageName);
        Collection<URL> urls = ClasspathHelper.forClassLoader(classLoader);
        configurationBuilder.addUrls(urls.stream().filter(Objects::nonNull).collect(Collectors.toList()));
        configurationBuilder.addScanners(new SubTypesScanner());
        Reflections reflections = new Reflections(configurationBuilder);
        Set<? extends Class<? extends R>> subClasses = reflections.getSubTypesOf(clazz).stream()
                .filter(c -> !ClassUtils.isInterfaceOrAbstract(c)).collect(Collectors.toSet());
        if(subClasses.isEmpty()){
            // Put null, so just search it in one time.
            cacheMap.put(refKey, null);
            return newRef(clazz);
        } else {
            Set<? extends Class<? extends R>> realSubClasses = subClasses;
            if (subClasses.size() > 1) {
                LOGGER.warn("subClass of {} size is {}, classes are {}", clazz.getName(), subClasses.size(), subClasses);
                realSubClasses = subClasses.stream().filter(subClass -> subClass.getName().contains(packageName) && !((Class) subClass).isInterface()).collect(Collectors.toSet());
                LOGGER.warn("realSubClasses is {} ", realSubClasses);
            }
            if (realSubClasses.size() > 1){
                LOGGER.error("realSubClasses size is bigger than 1");
                DSSExceptionUtils.dealErrorException(60091, "too many subclass of " + clazz.getName() + "in " + packageName,
                        DSSErrorException.class);
            } else if(realSubClasses.size() == 0) {
                // Put null, so just search it in one time.
                cacheMap.put(refKey, null);
                return newRef(clazz);
            }
            Class<? extends R> subClass = realSubClasses.iterator().next();
            cacheMap.put(refKey, subClass);
            return DSSExceptionUtils.tryAndWarn(Void -> subClass.newInstance());
        }
    }

}
