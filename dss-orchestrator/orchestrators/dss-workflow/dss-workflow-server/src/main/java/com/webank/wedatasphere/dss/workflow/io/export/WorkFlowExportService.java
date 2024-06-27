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

import com.webank.wedatasphere.dss.common.label.DSSLabel;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;

import java.util.List;

public interface WorkFlowExportService {
    /**
     * 将一个工作流导出成压缩包，存放到本地磁盘，并返回压缩包的路径
     * @param dssProjectId 所属工作空间id
     * @param projectName 所属项目名称
     * @param rootFlowId 工作流根节点id
     * @param userName 用户名
     * @param workspace 所属工作空间
     * @param dssLabels label列表
     * @param exportExternalNodeAppConnResource 是否导出第三方节点的物料
     * @return 导出的工作流压缩包的文件地址
     * @throws Exception
     */
    String exportFlowInfoNew(Long dssProjectId, String projectName, long rootFlowId, String userName, Workspace workspace,
                             List<DSSLabel> dssLabels,boolean exportExternalNodeAppConnResource) throws Exception;

    /**
     * 将一个工作流导出成压缩包，存放到本地磁盘，并返回压缩包的路径
     * @param dssProjectId 所属工作空间id
     * @param projectName 所属项目名称
     * @param rootFlowId 工作流根节点id
     * @param userName 用户名
     * @param workspace 所属工作空间
     * @param dssLabels label列表
     * @return 导出的工作流压缩包的文件地址
     * @throws Exception
     */
    String exportFlowInfo(Long dssProjectId, String projectName, long rootFlowId, String userName, Workspace workspace, List<DSSLabel> dssLabels) throws Exception;

    /**
     * 导出工作流中的各种资源，放到projectSavePath中。
     * 工作流中的资源包括工作流资源和节点资源
     * @param projectSavePath 保存资源的目录
     * @param flowJson 工作流元信息
     * @param flowName 工作流明
     * @param dssLabels label列表
     * @throws Exception
     */
    void exportFlowResources(String userName, Long projectId, String projectName, String projectSavePath, String flowJson, String flowName, Workspace workspace,List<DSSLabel> dssLabels) throws Exception;

    /**
     * 从bml中取出工作流的元数据信息
     * @param userName 执行用户
     * @param resourceId 工作流元数据的resourceId
     * @param version 工作流元数据信息bml文件的版本
     * @param savePath 取出后的元数据信息存放目录
     * @return 工作流的元数据信息
     */
    String downloadFlowJsonFromBml(String userName, String resourceId, String version, String savePath);
}
