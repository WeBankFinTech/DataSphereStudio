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

package com.webank.wedatasphere.dss.framework.project.contant;

public enum  ProjectServerResponse {
    PROJECT_NOT_EDIT_AUTH(6001,"没有修改权限"),
    PROJECT_NOT_EDIT_NAME(6002,"不能修改工程名称"),
    PROJECT_NOT_EXIST(6003,"工程不存在"),
    PROJECT_USER_NOT_IN_WORKSPACE(6004,"只有工作空间用户或管理员才能创建工程"),
    PROJECT_IS_NOT_ADMIN_OR_RELEASE(6005,"只有创建人、管理员或发布者权限用户才能编辑工程")
    ;

    ProjectServerResponse(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private int code;
    private String msg;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
