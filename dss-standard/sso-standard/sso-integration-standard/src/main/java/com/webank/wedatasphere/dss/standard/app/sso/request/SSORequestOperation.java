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

package com.webank.wedatasphere.dss.standard.app.sso.request;

import com.webank.wedatasphere.dss.standard.common.exception.AppStandardErrorException;
import com.webank.wedatasphere.dss.standard.common.service.Operation;
import com.webank.wedatasphere.dss.standard.app.sso.builder.SSOUrlBuilderOperation;

/**
 * Created by enjoyyin on 2020/9/3.
 */
public interface SSORequestOperation<T, R> extends Operation {

    R requestWithSSO(SSOUrlBuilderOperation urlBuilder, T req) throws AppStandardErrorException;

    R requestWithSSO(String url, T req) throws AppStandardErrorException;

}
