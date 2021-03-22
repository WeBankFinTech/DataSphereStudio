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

import java.io.Serializable;
import java.util.Map;

/**
 * created by cooperyang on 2020/3/8
 * Description:
 */
public class DSSWorkspaceComponentPrivVO extends AbstractDSSVO implements Serializable {
    private static final long serialVersionUID=1L;

    private int id;
    private String name;
    private Map<String, Boolean> componentPrivs;


    public DSSWorkspaceComponentPrivVO() {
    }

    public DSSWorkspaceComponentPrivVO(int id, String name, Map<String, Boolean> componentPrivs) {
        this.id = id;
        this.name = name;
        this.componentPrivs = componentPrivs;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Boolean> getComponentPrivs() {
        return componentPrivs;
    }

    public void setComponentPrivs(Map<String, Boolean> componentPrivs) {
        this.componentPrivs = componentPrivs;
    }

    @Override
    public String toString() {
        return "DSSWorkspaceComponentPrivVO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", componentPrivs=" + componentPrivs +
                '}';
    }
}
