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

package com.webank.wedatapshere.dss.appconn.datachecker;

import com.webank.wedatasphere.dss.standard.app.development.execution.common.CompletedExecutionResponseRef;

import java.util.Map;

/**
 * @author allenlliu
 * @date 2021/1/26 16:37
 */
public class DataCheckerCompletedExecutionResponseRef extends CompletedExecutionResponseRef {

    private Exception exception;
    public void setException(Exception exception) {
        this.exception = exception;
    }



    public DataCheckerCompletedExecutionResponseRef(int status) {
        super(status);
    }

    public DataCheckerCompletedExecutionResponseRef(String responseBody, int status) {
        super(responseBody, status);
    }

    public void setStatus(int status){
        this.status = status;
    }


    @Override
    public Map<String, Object> toMap() {
        return null;
    }

}
