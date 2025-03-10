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

package com.webank.wedatasphere.dss.framework.project.entity.request;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class ProjectDeleteOrRestoreRequest {

    @NotNull(message = "工程id不能为空")
    private Long id;

    @NotNull(message = "确认不能为空")
    private boolean sure;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isSure() {
        return sure;
    }

    public void setSure(boolean sure) {
        this.sure = sure;
    }



    @Override
    public String toString() {
        return "ProjectDeleteRequest{" +
                "id=" + id +
                ", sure=" + sure +
                '}';
    }
}
