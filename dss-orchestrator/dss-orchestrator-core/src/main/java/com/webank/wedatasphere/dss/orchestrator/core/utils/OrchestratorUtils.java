/*
 *
 *  * Copyright 2019 WeBank
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.webank.wedatasphere.dss.orchestrator.core.utils;

/**
 * @author allenlliu
 * @date 2020/12/4 10:24
 */
public class OrchestratorUtils {

        public static String generateNewVersion() {
            return "v000001";
        }

        /**
         * 注意： flow版本更新需要同步更新ContextID
         * @param oldVersion
         * @return
         */
        public static String increaseVersion(String oldVersion) {
            int num = Integer.parseInt(oldVersion.substring(1)) + 1;
            String tmp = "00000" + num;
            return "v" + tmp.substring(tmp.length() - 6);
        }


}
