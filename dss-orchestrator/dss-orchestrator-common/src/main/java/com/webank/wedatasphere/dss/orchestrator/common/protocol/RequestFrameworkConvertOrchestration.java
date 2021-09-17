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


public class RequestFrameworkConvertOrchestration {

    private String userName;
    private DSSWorkspace workspace;
    private Long orcAppId;
    private List<Long> orcIds;
    private Map<String, Object> labels;
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
