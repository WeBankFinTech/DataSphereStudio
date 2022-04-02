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

package com.webank.wedatasphere.dss.appconn.eventchecker.utils;

import com.webank.wedatasphere.dss.appconn.eventchecker.execution.EventCheckerExecutionAction;

/**
 * A util helper class full of static methods that are commonly used.
 */
public class Utils {
    /**
     * Equivalent to Object.equals except that it handles nulls. If a and b are both null, true is
     * returned.
     */
    public static boolean equals(final Object a, final Object b) {
        if (a == null || b == null) {
            return a == b;
        }

        return a.equals(b);
    }

    public static void log(EventCheckerExecutionAction nodeContext, String log) {
        nodeContext.getExecutionRequestRefContext().appendLog(log);
    }

    public static void log(EventCheckerExecutionAction nodeContext, Exception e) {
        nodeContext.getExecutionRequestRefContext().appendLog(ExceptionUtils.stacktraceToOneLineString(e,3000));
    }
}
