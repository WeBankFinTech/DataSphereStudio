/*
 * Copyright (C) 2006-2016 AiJia All rights reserved
 * Author: chongchuanbing
 * Date: 2019-06-28
 * Description:ModelMapperUtil.java
 */
package com.webank.wedatasphere.linkis.oneservice.core.util;

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
