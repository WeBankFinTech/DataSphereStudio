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

package com.webank.wedatasphere.dss.framework.project.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;



public class LambdaHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(LambdaHelper.class);

    public static <T> Consumer<T> consumerWrapper(Consumer<T> consumer){
        return t -> {
            try{
                consumer.accept(t);
            }catch(Exception e){
                LOGGER.error("Failed to do with {} ", t, e);
            }
        };
    }


}
