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

import com.webank.wedatasphere.dss.standard.app.structure.AbstractStructureService;

/**
 * 实现了工程的统一创建、更新、删除和查重操作。
 * 用于打通 DSS 工程与接入的第三方应用工具的工程体系，实现工程的协同管理。
 */
public abstract class ProjectService extends AbstractStructureService {

    /**
     * This method is used to ensure if the third-part AppConn has the same permission
     * management with DSS project or not.
     * <br>
     * If permission management is exists and similar, please return true; otherwise
     * please return false.
     * @return return false by default.
     */
    public boolean isCooperationSupported() {
        return false;
    }

    /**
     * This is a pretty important method, please be aware of the instruction of this method.
     * This method is used to ensure Whether the third-part AppConn allows duplicate project names.
     * If this method returns false, which means duplicate project names is allowed, no exception will
     * be throwed by DSS project framework when
     * a new DSS project is creating and a duplicate project name check was not passed; otherwise, the
     * project creation will be failed if a duplicate project name check was not passed.
     * <br>
     * 这是一个非常重要的方法，请注意此方法的说明。
     * 此方法用于确保第三方 AppConn 是否允许重复的项目名称。
     * 如果该方法返回 false，即允许项目名重复，则在创建新的 DSS 项目并进行项目名重复性检查时，
     * 即使第三方应用存在同名项目，DSS 框架也不会抛出异常；
     * 否则，如果项目名重复性检查未通过，则 DSS 项目将创建失败。
     * @return return true by default, means if the third-part AppConn has exist a project with the same
     * name, the DSS project creation will be failed(默认返回 true，表示如果第三方 AppConn 已经存在同名项目，
     * 则 DSS 项目将创建失败).
     */
    public boolean isProjectNameUnique() {
        return true;
    }

    public final ProjectCreationOperation getProjectCreationOperation() {
        return getOrCreate(this::createProjectCreationOperation, ProjectCreationOperation.class);
    }

    protected abstract ProjectCreationOperation createProjectCreationOperation();

    public final ProjectUpdateOperation getProjectUpdateOperation() {
        return getOrCreate(this::createProjectUpdateOperation, ProjectUpdateOperation.class);
    }

    protected abstract ProjectUpdateOperation createProjectUpdateOperation();

    public final ProjectDeletionOperation getProjectDeletionOperation() {
        return getOrCreate(this::createProjectDeletionOperation, ProjectDeletionOperation.class);
    }

    protected abstract ProjectDeletionOperation createProjectDeletionOperation();


    public final ProjectSearchOperation getProjectSearchOperation() {
        return getOrCreate(this::createProjectSearchOperation, ProjectSearchOperation.class);
    }

    protected abstract ProjectSearchOperation createProjectSearchOperation();

}
