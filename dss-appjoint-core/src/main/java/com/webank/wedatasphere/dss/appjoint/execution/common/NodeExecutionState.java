/*
 * Copyright 2019 WeBank
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
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

package com.webank.wedatasphere.dss.appjoint.execution.common;

/**
 * created by enjoyyin on 2019/9/25
 * Description:
 */
public enum NodeExecutionState {
    /**
     * NodeExecution的状态枚举
     */
    Accepted(0, "Accepted"),
    Running(1, "Running"),
    Success(2, "Success"),
    Failed(3, "Failed"),
    Killed(4, "Killed"),
    Alert(5, "Alert");

    private int code;
    private String status;

    private NodeExecutionState(int code, String status){
        this.code = code;
        this.status = status;
    }

    public int getCode() {
        return code;
    }

    public String getStatus() {
        return status;
    }

    public static boolean isCompleted(NodeExecutionState state){
        return Success.equals(state) || Failed.equals(state) || Killed.equals(state);
    }

    public static boolean isCompleted(String state){
        return Success.status.equals(state) ||
                Failed.status.equals(state) ||
                Killed.status.equals(state);
    }

    public boolean isCompleted(){
        return isCompleted(this);
    }

    public boolean isSuccess(){
        return Success.equals(this);
    }



}
