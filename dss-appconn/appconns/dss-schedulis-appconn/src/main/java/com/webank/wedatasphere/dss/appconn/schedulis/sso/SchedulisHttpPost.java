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

package com.webank.wedatasphere.dss.appconn.schedulis.sso;

import org.apache.http.client.methods.HttpPost;

/**
 * created by cooperyang on 2021/1/15
 * Description:
 */
public class SchedulisHttpPost extends HttpPost implements UserInfo{

    private String user;

    public SchedulisHttpPost(String url, String user){
        super(url);
        this.user = user;
    }

    @Override
    public String getUser() {
        return user;
    }


}
