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

package com.webank.wedatasphere.dss.common.exception;

import com.webank.wedatasphere.linkis.common.exception.WarnException;

/**
 * Created by v_wbjftang on 2019/9/24.
 */
public class DSSRuntimeException extends WarnException {

    public DSSRuntimeException(String msg){
        super(100000, msg);
    }

    public DSSRuntimeException(int errorCode, String msg) {
        super(errorCode, msg);
    }

    public DSSRuntimeException(int errorCode, String msg, Throwable e) {
        super(errorCode, msg);
        initCause(e);
    }
}
