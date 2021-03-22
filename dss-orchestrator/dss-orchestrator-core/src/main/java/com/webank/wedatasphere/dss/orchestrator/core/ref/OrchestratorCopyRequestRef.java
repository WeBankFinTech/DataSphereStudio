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

package com.webank.wedatasphere.dss.orchestrator.core.ref;

import com.webank.wedatasphere.dss.standard.app.development.crud.CopyRequestRef;

/**
 * @author allenlliu
 * @date 2020/12/7 14:46
 */
public interface OrchestratorCopyRequestRef extends CopyRequestRef {

    void setCopyOrcAppId(long appId);

    void setCopyOrcVersionId(long orcId);

    default void setUserName(String username){

    }




}
