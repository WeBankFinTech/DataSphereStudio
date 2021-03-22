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

package com.webank.wedatasphere.dss.workflow.io.export;

import com.webank.wedatasphere.dss.common.entity.node.DSSNode;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;

/**
 * @author allenlliu
 * @version 2.0.0
 * @date 2020/03/09 02:55 PM
 */
public interface NodeExportService {
    void downloadNodeResourceToLocal(String userName, DSSNode dwsNode, String savePath);
    void downloadAppjointResourceToLocal(String userName, Long projectId, DSSNode dwsNode, String savePath, Workspace workspace) throws Exception;
}
