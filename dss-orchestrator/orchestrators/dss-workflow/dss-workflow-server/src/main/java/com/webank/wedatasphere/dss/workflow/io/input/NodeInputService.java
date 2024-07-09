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

package com.webank.wedatasphere.dss.workflow.io.input;


import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.common.label.DSSLabel;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.workflow.common.entity.DSSFlow;

import java.io.IOException;
import java.util.List;

public interface NodeInputService {

    String uploadResourceToBml(String userName, String nodeJson, String inputResourcePath, String projectName) throws IOException;

    String uploadResourceToBmlNew(String userName, String nodeJson, String nodePath, String nodeName,String projectName) throws IOException;

    String uploadAppConnResource(String userName, String projectName, DSSFlow dssFlow,
                                 String nodeJson, String flowContextId, String appConnResourcePath,
                                 Workspace workspace, String orcVersion, List<DSSLabel> dssLabels) throws DSSErrorException,IOException;
    String uploadAppConnResourceNew(String userName, String projectName, DSSFlow dssFlow,
                                 String nodeJson, String flowContextId, String nodePath,
                                 Workspace workspace, String orcVersion, List<DSSLabel> dssLabels) throws DSSErrorException,IOException;

    String updateNodeSubflowID(String nodeJson, long subflowID) throws IOException;

}
