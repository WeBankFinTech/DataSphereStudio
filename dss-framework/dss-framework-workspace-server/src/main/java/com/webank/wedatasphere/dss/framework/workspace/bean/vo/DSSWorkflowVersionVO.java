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

package com.webank.wedatasphere.dss.framework.workspace.bean.vo;

import java.util.Arrays;
import java.util.List;

/**
 * created by cooperyang on 2020/3/8
 * Description:
 */
public class DSSWorkflowVersionVO {
    private int workflowId;
    private String version;
    private String creator;
    private String comment;
    private String createTime;
    private String workflowJson;

    public DSSWorkflowVersionVO() {
    }

    public DSSWorkflowVersionVO(int workflowId, String version, String creator, String comment, String createTime, String workflowJson) {
        this.workflowId = workflowId;
        this.version = version;
        this.creator = creator;
        this.comment = comment;
        this.createTime = createTime;
        this.workflowJson = workflowJson;
    }

    public int getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(int workflowId) {
        this.workflowId = workflowId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getWorkflowJson() {
        return workflowJson;
    }

    public void setWorkflowJson(String workflowJson) {
        this.workflowJson = workflowJson;
    }

    public static void main(String[] args){

        DSSWorkflowVersionVO vo1 =
                new DSSWorkflowVersionVO(123, "v0001", "enjoyyin", "第一次的版本", "2020-03-08 13:47:01", "工作流json");
        DSSWorkflowVersionVO vo2 =
                new DSSWorkflowVersionVO(123, "v0002", "cooperyang", "第二次的版本", "2020-03-08 14:47:01", "工作流json");

        List<DSSWorkflowVersionVO> list = Arrays.asList(vo1, vo2);
        System.out.println(VOUtils.gson.toJson(list));
    }

}
