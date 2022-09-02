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

package com.webank.wedatasphere.dss.standard.app.development.ref;


/**
 * 若AppConn实现了二级规范，则在实现CopyRequestRef的同时必须要实现ProjectRequestRef。
 * 原因是dss调用节点复制接口时，传的refProjectId可能是节点源工程id（同一个项目内拷贝），也可能是另一个工程Id（跨项目拷贝）。
 *
 * @param <R>
 */
public interface CopyRequestRef<R extends RefJobContentRequestRef<R>>
        extends RefJobContentRequestRef<R> {

    default R setNewVersion(String version) {
        setParameter("newVersion", version);
        return (R) this;
    }

    /**
     * The new version comes from DSS Orchestrator framework.
     * When the orchestrator, such as DSSWorkflow, added a new version, we hope all nodes of this workflow,
     * can also update a new version.
     * <p>
     * 特别注意：
     * 此版本号格式不一定是v000001的格式，在工作流复制操作时会加上前缀或者后缀（suffix_v000001的形式），三方节点最好将其当做一个普通字符串处理。
     * 三方节点新增节点版本时，需要将之前版本号去掉后缀，然后用节点名前缀拼接新的版本号。
     * 比如节点名：widget_1001_v000001 在新增版本号（v000002）后需变为：widget_1001_v000002，而不是widget_1001_v000001_v000002
     *
     * @return the new version of the orchestrator, such as DSSWorkflow.
     */
    default String getNewVersion() {
        return (String) getParameter("newVersion");
    }

}
