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

package com.webank.wedatasphere.dss.appconn.schedulis.standard;

import com.webank.wedatasphere.dss.appconn.schedulis.service.SchedulisProjectService;
import com.webank.wedatasphere.dss.standard.app.structure.AbstractStructureIntegrationStandard;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectService;


/**
 * Schedulis's engineering integration specification is a singleton.
 */
public class SchedulisStructureStandard  extends AbstractStructureIntegrationStandard {

    private volatile static SchedulisStructureStandard instance;

    private SchedulisStructureStandard(){
    }

    public static SchedulisStructureStandard getInstance(){
        if(instance == null){
            synchronized (SchedulisStructureStandard.class){
                if (instance == null){
                    instance = new SchedulisStructureStandard();
                }
            }
        }
        return instance;
    }

    @Override
    protected ProjectService createProjectService() {
        return new SchedulisProjectService();
    }
}
