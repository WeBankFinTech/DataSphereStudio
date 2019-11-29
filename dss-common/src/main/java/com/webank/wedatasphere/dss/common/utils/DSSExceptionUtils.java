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

package com.webank.wedatasphere.dss.common.utils;

import com.webank.wedatasphere.dss.common.exception.DSSRuntimeException;
import com.webank.wedatasphere.dss.common.exception.ThrowingConsumer;
import com.webank.wedatasphere.dss.common.exception.ThrowingFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by enjoyyin on 2019/9/24.
 */
public class DSSExceptionUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(DSSExceptionUtils.class);

    public static <T, E extends Exception> Consumer<T> handling(
            ThrowingConsumer<T, E> throwingConsumer) {
        return i -> {
            try {
                throwingConsumer.accept(i);
            } catch (Exception e) {
                LOGGER.error("execute failed,reason:",e);
                throw new DSSRuntimeException(e.getMessage());
            }
        };
    }

    public static <T,R, E extends Exception> Function<T,R> map(
            ThrowingFunction<T,R, E> throwingFunction) {
        return i -> {
            try {
                return throwingFunction.accept(i);
            } catch (Exception e) {
                LOGGER.error("execute failed,reason:",e);
                throw new DSSRuntimeException(e.getMessage());
            }
        };
    }
}
