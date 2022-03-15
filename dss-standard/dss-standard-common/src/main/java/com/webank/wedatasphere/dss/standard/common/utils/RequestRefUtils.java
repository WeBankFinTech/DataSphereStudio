package com.webank.wedatasphere.dss.standard.common.utils;

import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.common.utils.ClassUtils;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.stream.Stream;

/**
 * @author enjoyyin
 * @date 2022-03-11
 * @since 0.5.0
 */
public class RequestRefUtils {

    public static <T> T getRequestRef(Object operation) {
        ParameterizedType parameterizedType = (ParameterizedType) operation.getClass().getGenericSuperclass();
        return newInstance(operation, parameterizedType);
    }

    public static <T> T getRequestRef(Object operation, Class<T> clazz) {
        return Stream.of(operation.getClass().getGenericInterfaces())
            .filter(c -> clazz.isAssignableFrom((Class<?>) c)).map(c -> {
                ParameterizedType parameterizedType = (ParameterizedType) c;
                return (T) newInstance(operation, parameterizedType);
        }).findAny().orElseThrow(() -> new ExternalOperationFailedException(50063, "Cannot find the real requestRef of " + operation.getClass() + "."));
    }

    private static <T> T newInstance(Object operation, ParameterizedType parameterizedType) {
        Type[] types = parameterizedType.getActualTypeArguments();
        if(types.length > 0 && types.length <= 2) {
            Class<T> t = (Class<T>) types[0];
            try {
                return ClassUtils.getInstance(t);
            } catch (DSSErrorException e) {
                throw new ExternalOperationFailedException(50063, "create the instance of " + types[0] + " failed.", e);
            }
        } else {
            throw new ExternalOperationFailedException(50063, "Cannot find the real requestRef of " + operation.getClass().getSimpleName() + ".");
        }
    }

}
