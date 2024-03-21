package com.webank.wedatasphere.dss.framework.workspace.bean;

import java.util.List;

public class DSSUserRoleComponentPriv {
    private String userId;
    private String userName;
    private List<RoleInfo> roles;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public List<RoleInfo> getRoles() {
        return roles;
    }

    public void setRoles(List<RoleInfo> roles) {
        this.roles = roles;
    }

    public static class RoleInfo{
        /**
         * 角色编码（英文或数字组成的字符串，唯一编码），格式：workspaceId + "-" + roleId
         */
        private String roleCode;
        /**
         * 角色名称（唯一编码），格式：workspaceName + "-" + roleName
         */
        private String roleName;
        /**
         * 角色中文名，格式：workspaceName + "-" + roleFrontName
         */
        private String roleNameCn;
        private List<PrivInfo> privs;

        public String getRoleCode() {
            return roleCode;
        }

        public void setRoleCode(String roleCode) {
            this.roleCode = roleCode;
        }

        public String getRoleName() {
            return roleName;
        }

        public void setRoleName(String roleName) {
            this.roleName = roleName;
        }

        public String getRoleNameCn() {
            return roleNameCn;
        }

        public void setRoleNameCn(String roleNameCn) {
            this.roleNameCn = roleNameCn;
        }

        public List<PrivInfo> getPrivs() {
            return privs;
        }

        public void setPrivs(List<PrivInfo> privs) {
            this.privs = privs;
        }

        public static class PrivInfo{
            private String privCode;
            private String privName;
            private String privNameCn;
            private String privContent;
            /**
             * 权限类型
             * 可选值："data", "module", "group", "view", "action", "button", "dataAuth", "product"，根据需要自定义即可，如按钮权限为button
             */
            private String privType;

            public String getPrivCode() {
                return privCode;
            }

            public void setPrivCode(String privCode) {
                this.privCode = privCode;
            }

            public String getPrivName() {
                return privName;
            }

            public void setPrivName(String privName) {
                this.privName = privName;
            }

            public String getPrivNameCn() {
                return privNameCn;
            }

            public void setPrivNameCn(String privNameCn) {
                this.privNameCn = privNameCn;
            }

            public String getPrivContent() {
                return privContent;
            }

            public void setPrivContent(String privContent) {
                this.privContent = privContent;
            }

            public String getPrivType() {
                return privType;
            }

            public void setPrivType(String privType) {
                this.privType = privType;
            }
        }
    }

}
