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

package com.webank.wedatasphere.dss.appjoint.auth;

import com.webank.wedatasphere.dss.appjoint.auth.impl.AppJointAuthImpl$;

import javax.servlet.http.HttpServletRequest;
import java.io.Closeable;

/**
 * Created by enjoyyin on 2019/11/6.
 */
public interface AppJointAuth extends Closeable {

    boolean isDssRequest(HttpServletRequest request);

    RedirectMsg getRedirectMsg(HttpServletRequest request);

    static AppJointAuth getAppJointAuth() {
        return AppJointAuthImpl$.MODULE$.apply();
    }

}
