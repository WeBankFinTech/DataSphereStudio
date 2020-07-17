/*
 * Copyright 2019 WeBank
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
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
package com.webank.wedatasphere.dss.oneservice.core.util;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author chongchuanbing
 */
public class ModelMapperUtil {
    private static ModelMapper modelMapper = new ModelMapper();

    public ModelMapperUtil() {
    }

    public static <D> D strictMap(Object source, Class<D> destinationType) {
        return modelMapper.map(source, destinationType);
    }

    public static <D> List<D> strictMapList(Object source, Class<D> componentType) {
        List<D> list = new ArrayList();
        List<Object> objectList = (List)source;
        Iterator var4 = objectList.iterator();

        while(var4.hasNext()) {
            Object obj = var4.next();
            list.add(modelMapper.map(obj, componentType));
        }

        return list;
    }

    static {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }
}
