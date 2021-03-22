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

package com.webank.wedatasphere.dss.workflow.io.input;


import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.workflow.common.entity.DSSFlow;

import java.io.IOException;

/**
 * @author allenlliu
 * @version 2.0.0
 * @date 2020/03/10 03:02 PM
 */
public interface NodeInputService {
    String uploadResourceToBml(String userName, String nodeJson, String inputResourcePath, String projectName) throws IOException;
    String uploadAppjointResource(String userName, String projectName, DSSFlow DSSFlow, String nodeJson, String flowContextId, String appjointResourcePath, Workspace workspace, String orcVerson) throws  IOException;
    String updateNodeSubflowID(String nodeJson, long subflowID) throws IOException;

}
