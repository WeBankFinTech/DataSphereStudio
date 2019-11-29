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

package com.webank.wedatasphere.dss.application.service;


import com.webank.wedatasphere.dss.appjoint.AppJoint;
import com.webank.wedatasphere.dss.appjoint.exception.AppJointErrorException;
import com.webank.wedatasphere.dss.application.entity.Application;

import java.util.List;

/**
 * Created by chaogefeng on 2019/10/10.
 */
public interface ApplicationService {

    List<String> listApplicationNames();

    List<Application> listApplications();

    Application getApplication(String appName);

    AppJoint getAppjoint(String nodeType) throws AppJointErrorException;

    List<AppJoint> listAppjoint() throws AppJointErrorException;

    Application getApplicationbyNodeType(String nodeType);
}
