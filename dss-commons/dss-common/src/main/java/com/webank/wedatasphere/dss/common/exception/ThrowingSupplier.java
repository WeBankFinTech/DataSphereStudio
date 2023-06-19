package com.webank.wedatasphere.dss.common.exception;

/**
 * @author enjoyyin
 * @date 2022-07-20
 * @since 1.1.1
 */
@FunctionalInterface
public interface ThrowingSupplier<T, E extends Exception> {

    T get() throws E;

}
