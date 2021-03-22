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

package com.webank.wedatasphere.dss.linkis.node.execution.job;

/**
 * Created by enjoyyin on 2019/11/13.
 */
public interface SignalSharedJob extends SharedJob {

    String PREFIX = "signal.";

    JobSignalKeyCreator getSignalKeyCreator();

    void setSignalKeyCreator(JobSignalKeyCreator signalKeyCreator);

    @Override
    default int getSharedNum() {
        return  -1;
    }

    @Override
    default String getSharedKey() {
        return PREFIX + getSignalKeyCreator().getSignalKeyBySignalSharedJob(this) + "." + getMsgSaveKey();
    }

    String getMsgSaveKey();
}
