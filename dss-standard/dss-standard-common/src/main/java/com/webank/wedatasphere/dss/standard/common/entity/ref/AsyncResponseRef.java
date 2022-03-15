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

import java.util.function.Consumer;

/**
 * This is the topper interface of async responseRef, if one operation want to provide async capability,
 * please provides a child interface which extends this super interface to use.
 * Notice: all detail classes should never directly implement this topper interface, since every operation will
 * provide a child interface for async operation.
 */
public interface AsyncResponseRef extends ResponseRef {

    long getStartTime();

    default boolean isCompleted() {
        return isFailed() || isSucceed();
    }

    ResponseRef getResponse();

    void waitForCompleted() throws InterruptedException;

    void notifyMe(Consumer<ResponseRef> notifyListener);

}
