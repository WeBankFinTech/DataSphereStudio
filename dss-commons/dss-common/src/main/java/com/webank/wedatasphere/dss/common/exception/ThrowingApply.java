package com.webank.wedatasphere.dss.common.exception;

/**
 * @author enjoyyin
 * @date 2022-07-20
 * @since 1.1.1
 */
@FunctionalInterface
public interface ThrowingApply<E extends Exception> {

    void apply() throws E;

}
