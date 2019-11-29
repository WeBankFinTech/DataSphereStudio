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
public class CompletedNodeExecutionResponse implements NodeExecutionResponse {

    private boolean isSucceed;

    private String errorMsg;

    private String succeedMsg;

    private Exception exception;

    public void setIsSucceed(boolean isSucceed){
        this.isSucceed = isSucceed;
    }

    public boolean isSucceed() {
        return this.isSucceed;
    }

    public String getErrorMsg(){
        return this.errorMsg;
    }

    public void setErrorMsg(String errorMsg){
        this.errorMsg = errorMsg;
    }


    public String getSucceedMsg() {
        return succeedMsg;
    }

    public void setSucceedMsg(String succeedMsg) {
        this.succeedMsg = succeedMsg;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }
}
