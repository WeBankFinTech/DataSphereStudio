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

package com.webank.wedatasphere.dss.workflow.io.export;

import com.webank.wedatasphere.dss.common.entity.node.DSSNode;
import com.webank.wedatasphere.dss.common.label.DSSLabel;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;

import java.util.List;

public interface NodeExportService {
    void downloadNodeResourceToLocal(String userName, DSSNode dwsNode, String savePath);
    void downloadAppConnResourceToLocal(String userName, Long projectId, String projectName, DSSNode dwsNode, String savePath, Workspace workspace, List<DSSLabel> dssLabels) throws Exception;
    void downloadNodeResourceToLocalNew(String userName, DSSNode dwsNode, String flowCodePath);
    void downloadAppConnResourceToLocalNew(String userName, Long projectId, String projectName, DSSNode dwsNode, String flowCodePath, Workspace workspace, List<DSSLabel> dssLabels) throws Exception;
}
