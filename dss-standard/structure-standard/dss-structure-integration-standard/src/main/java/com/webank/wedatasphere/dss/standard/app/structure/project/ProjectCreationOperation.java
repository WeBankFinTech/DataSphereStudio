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

package com.webank.wedatasphere.dss.standard.app.structure.project;

import com.webank.wedatasphere.dss.standard.app.structure.StructureOperation;
import com.webank.wedatasphere.dss.standard.app.structure.project.ref.DSSProjectContentRequestRef;
import com.webank.wedatasphere.dss.standard.app.structure.project.ref.ProjectResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;


public interface ProjectCreationOperation<R extends DSSProjectContentRequestRef<R>>
        extends StructureOperation<R, ProjectResponseRef> {

    /**
     * Try to create the one-to-one related refProject in third-party AppConn.
     * If created successfully, please return a ProjectResponseRef contained refProjectId,
     * so DSS can use the refProjectId to operate the related refProject in third-party AppConn.
     * The returned refProjectId is the other ProjectOperations which used.
     * <br><br>
     * 该方法会尝试请求第三方应用工具创建一个与 DSS 工程一对一关联的第三方 refProject。
     * 如果创建成功，请返回一个包含了第三方应用工具的工程 ID（命名为 refProjectId）的 ProjectResponseRef，
     * 以便 DSS 接下来可以使用 refProjectId 来管理第三方应用工具的这个工程（命名为 refProject）。
     * <br>
     * 返回的 refProjectId 是其他 ProjectOperation 能够操作这个第三方应用工具的工程的基础。DSS 在调用其他
     * ProjectOperation 时，会将该 refProjectId 作为方法参数传入，以便用户能正常找到对应的 refProject进行相应操作。
     * @param projectRef contains the DSS project info(包含了 DSS 的工程信息).
     * @return a ProjectResponseRef contained refProjectId(返回一个包含了 refProjectId 的 ProjectResponseRef)
     * @throws ExternalOperationFailedException 如果创建过程中发生异常，请抛出该异常。
     */
    ProjectResponseRef createProject(R projectRef) throws ExternalOperationFailedException;

}
