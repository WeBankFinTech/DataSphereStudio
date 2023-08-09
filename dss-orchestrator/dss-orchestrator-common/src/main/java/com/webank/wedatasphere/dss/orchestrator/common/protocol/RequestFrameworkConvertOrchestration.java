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

package com.webank.wedatasphere.dss.orchestrator.common.protocol;

import com.webank.wedatasphere.dss.common.entity.DSSWorkspace;
import java.util.List;
import java.util.Map;

/**
 * 编排转化为调度工作流（发布）的请求类。
 * 可以发布单个工作流，多个编排，也可以是发布整个工程。
 */
public class RequestFrameworkConvertOrchestration {

    private String userName;
    private DSSWorkspace workspace;
    /**
     * 可以指定一个编排实现的id，比如工作流的id，来发布这个编排
     */
    private Long orcAppId;
    /**
     * 也可以直接指定要发布的编排列表
     */
    private List<Long> orcIds;
    /**
     * 工程id。如果工程id不为空，则表示要发布这个工程下所有未发布过以及已发布过
     */
    private Long projectId;
    private Map<String, Object> labels;
    /**
     * 关联的审批单号
     */
    private String approveId;

    /**
     * 是否要发布工程下所有的编排。
     * 如果为true，那么不仅仅会发布{@link #orcIds }或者{@link #orcAppId}指定的编排，还会发布对应工程下其他所有的编排
     */
    private boolean convertAllOrcs;
    private String comment;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public DSSWorkspace getWorkspace() {
        return workspace;
    }

    public void setWorkspace(DSSWorkspace workspace) {
        this.workspace = workspace;
    }

    public Long getOrcAppId() {
        return orcAppId;
    }

    public void setOrcAppId(Long orcAppId) {
        this.orcAppId = orcAppId;
    }

    public List<Long> getOrcIds() {
        return orcIds;
    }

    public void setOrcIds(List<Long> orcIds) {
        this.orcIds = orcIds;
    }
    public String getApproveId() {
        return approveId;
    }

    public void setApproveId(String approveId) {
        this.approveId = approveId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Map<String, Object> getLabels() {
        return labels;
    }

    public void setLabels(Map<String, Object> labels) {
        this.labels = labels;
    }

    public boolean isConvertAllOrcs() {
        return convertAllOrcs;
    }

    public void setConvertAllOrcs(boolean convertAllOrcs) {
        this.convertAllOrcs = convertAllOrcs;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }


}
