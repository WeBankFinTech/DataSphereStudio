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

package com.webank.wedatasphere.dss.appjoint.visualis.service;

import com.webank.wedatasphere.dss.appjoint.exception.AppJointErrorException;
import com.webank.wedatasphere.dss.appjoint.service.AppJointUrlImpl;
import com.webank.wedatasphere.dss.appjoint.service.SecurityService;
import com.webank.wedatasphere.dss.appjoint.service.session.Session;

/**
 * Created by shanhuang on 2019/10/12.
 */
public class VisualisSecurityService extends AppJointUrlImpl implements SecurityService {
    @Override
    public Session login(String user) throws AppJointErrorException {
        VisualisSession visualisSession = new VisualisSession();
        visualisSession.setUser(user);
        visualisSession.getParameters().put("Token-User",user);
        visualisSession.getParameters().put("Token-Code","172.0.0.1");
        return visualisSession;
    }

    @Override
    public void logout(String user) {

    }
}
