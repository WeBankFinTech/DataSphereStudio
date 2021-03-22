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

import java.util.List;
import java.util.Map;

/**
 * created by cooperyang on 2020/3/24
 * Description:
 */
public class DSSWorkspaceHomepageSettingVO {

    public static class RoleHomepage{
        private String roleName;
        private String homepageName;
        private String homepageUrl;
        private int roleId;
        private String roleFrontName;


        public String getRoleName() {
            return roleName;
        }

        public void setRoleName(String roleName) {
            this.roleName = roleName;
        }

        public String getHomepageName() {
            return homepageName;
        }

        public void setHomepageName(String homepageName) {
            this.homepageName = homepageName;
        }

        public String getHomepageUrl() {
            return homepageUrl;
        }

        public void setHomepageUrl(String homepageUrl) {
            this.homepageUrl = homepageUrl;
        }


        public int getRoleId() {
            return roleId;
        }

        public void setRoleId(int roleId) {
            this.roleId = roleId;
        }

        public String getRoleFrontName() {
            return roleFrontName;
        }

        public void setRoleFrontName(String roleFrontName) {
            this.roleFrontName = roleFrontName;
        }
    }

    private List<RoleHomepage> roleHomepages;

    public List<RoleHomepage> getRoleHomepages() {
        return roleHomepages;
    }

    public void setRoleHomepages(List<RoleHomepage> roleHomepages) {
        this.roleHomepages = roleHomepages;
    }


}
