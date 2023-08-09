package com.webank.wedatasphere.dss.common.utils;

import org.apache.linkis.common.exception.LinkisRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Process rpc exception
 */
public class RpcAskUtils {

    private static final Logger logger = LoggerFactory.getLogger(RpcAskUtils.class);

    public static <T, E> T processAskException(Object o, Class<T> responseClazz, Class<E> requestClazz) {
        if (o.getClass().equals(responseClazz)) {
            return responseClazz.cast(o);
        } else {
            logger.error(requestClazz.getSimpleName() + " failed for " + o);
            throw (LinkisRuntimeException) o;
        }
    }
}
