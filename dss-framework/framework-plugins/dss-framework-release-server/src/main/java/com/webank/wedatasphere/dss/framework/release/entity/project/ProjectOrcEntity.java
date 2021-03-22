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

package com.webank.wedatasphere.dss.framework.release.entity.project;

import com.google.gson.annotations.SerializedName;
import com.webank.wedatasphere.dss.framework.release.utils.ReleaseUtils;

import java.util.Date;
import java.util.List;

/**
 * created by cooperyang on 2020/12/11
 * Description: 导出Orchestrator信息之后,需要将Orchestartor的内容和Project的元数据信息进行整合
 * 可以是一个json的格式进行存储，然后进入到导入的orc framework
 */
public class ProjectOrcEntity {

    public static class InnerProject{
        Long projectId;
        String projectName;
        String createBy;
        String updateBy;
        String description;
        String clazzName;
        Date createTime;
        Date updateTime;

        public static InnerProject newInstance(Long projectId, String projectName, String createBy, String updateBy, String description, Date createTime){
            InnerProject innerProject = new InnerProject();
            innerProject.setProjectId(projectId);
            innerProject.setProjectName(projectName);
            innerProject.setUpdateBy(updateBy);
            innerProject.setCreateBy(createBy);
            innerProject.setDescription(description);
            innerProject.setCreateTime(createTime);
            innerProject.setUpdateBy(updateBy);
            innerProject.setCreateTime(new Date(System.currentTimeMillis()));
            return innerProject;
        }

        public Long getProjectId() {
            return projectId;
        }

        public void setProjectId(Long projectId) {
            this.projectId = projectId;
        }

        public String getProjectName() {
            return projectName;
        }

        public void setProjectName(String projectName) {
            this.projectName = projectName;
        }

        public String getCreateBy() {
            return createBy;
        }

        public void setCreateBy(String createBy) {
            this.createBy = createBy;
        }

        public String getUpdateBy() {
            return updateBy;
        }

        public void setUpdateBy(String updateBy) {
            this.updateBy = updateBy;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getClazzName() {
            return clazzName;
        }

        public void setClazzName(String clazzName) {
            this.clazzName = clazzName;
        }

        public Date getCreateTime() {
            return createTime;
        }

        public void setCreateTime(Date createTime) {
            this.createTime = createTime;
        }

        public Date getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(Date updateTime) {
            this.updateTime = updateTime;
        }
    }

    public static class InnerOrchestrator{
        String resourceId;
        String version;


        public static InnerOrchestrator newInstance(String resourceId,
                                                    String version){
            InnerOrchestrator innerOrchestrator = new InnerOrchestrator();
            innerOrchestrator.setResourceId(resourceId);
            innerOrchestrator.setVersion(version);
            return innerOrchestrator;
        }

        public String getResourceId() {
            return resourceId;
        }

        public void setResourceId(String resourceId) {
            this.resourceId = resourceId;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

    }


    @SerializedName("project")
    private InnerProject project;

    @SerializedName("orchestrators")
    private List<InnerOrchestrator> orchestrators;


    public InnerProject getProject() {
        return project;
    }

    public void setProject(InnerProject project) {
        this.project = project;
    }

    public List<InnerOrchestrator> getOrchestrators() {
        return orchestrators;
    }

    public void setOrchestrators(List<InnerOrchestrator> orchestrators) {
        this.orchestrators = orchestrators;
    }

    @Override
    public String toString() {
        return ReleaseUtils.GSON.toJson(this);
    }
}
