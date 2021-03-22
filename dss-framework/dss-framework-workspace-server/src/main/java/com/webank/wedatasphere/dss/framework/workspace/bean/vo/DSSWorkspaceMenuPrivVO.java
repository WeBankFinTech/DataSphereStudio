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
public class DSSWorkspaceMenuPrivVO extends AbstractDSSVO implements Serializable {
    private static final long serialVersionUID=1L;
    private int id;
    private String name;
    private Map<String, Boolean> menuPrivs;

    public DSSWorkspaceMenuPrivVO() {
    }

    public DSSWorkspaceMenuPrivVO(int id, String name, Map<String, Boolean> menuPrivs) {
        this.id = id;
        this.name = name;
        this.menuPrivs = menuPrivs;
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

    public Map<String, Boolean> getMenuPrivs() {
        return menuPrivs;
    }

    public void setMenuPrivs(Map<String, Boolean> menuPrivs) {
        this.menuPrivs = menuPrivs;
    }

    @Override
    public String toString() {
        return "DSSWorkspaceMenuPrivVO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", menuPrivs=" + menuPrivs +
                '}';
    }
}
