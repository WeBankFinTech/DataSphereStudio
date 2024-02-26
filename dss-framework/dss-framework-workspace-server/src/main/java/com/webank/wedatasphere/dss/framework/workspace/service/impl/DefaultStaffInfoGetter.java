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

package com.webank.wedatasphere.dss.framework.workspace.service.impl;

import com.google.common.collect.Lists;
import com.webank.wedatasphere.dss.framework.workspace.bean.StaffInfo;
import com.webank.wedatasphere.dss.framework.workspace.service.StaffInfoGetter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;



public class DefaultStaffInfoGetter implements StaffInfoGetter {

    @Override
    public List<StaffInfo> getAllUsers() {
        return Lists.newArrayList(new StaffInfo("1","hadoop", "WeDataSphere"));
    }

    @Override
    public String getFullOrgNameByUsername(String username) {
        return "WeDataSphere";
    }

    @Override
    public List<String> getAllDepartments() {
        List<String> allDepartments = Arrays.asList("WeDataSphere-linkisGroup");
        return allDepartments;
    }

    @Override
    public StaffInfo getStaffInfoByUsername(String username) {
        return new StaffInfo();
    }
}
