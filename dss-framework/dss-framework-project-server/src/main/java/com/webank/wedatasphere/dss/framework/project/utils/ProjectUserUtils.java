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

package com.webank.wedatasphere.dss.framework.project.utils;

import com.webank.wedatasphere.dss.framework.project.entity.DSSProjectUser;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ProjectUserUtils {


    public static List<DSSProjectUser> createPUser(Long wid, Long pid, List<String> users, int priv){
        List<DSSProjectUser> retList = new ArrayList<>();
        if(CollectionUtils.isEmpty(users)){
            return retList;
        }
        users.forEach(tname->{
            DSSProjectUser DSSProjectUser = new DSSProjectUser(wid, pid, tname,priv);
            retList.add(DSSProjectUser);
        });
        return retList;
    }


    public static List<String> getEditUserList(List<String> releaseUsers, List<String> editUsers){
        List<String> sumEditUsers = new ArrayList<>();
        if (!CollectionUtils.isEmpty(releaseUsers)) {
            sumEditUsers.addAll(releaseUsers);
        }
        if (!CollectionUtils.isEmpty(editUsers)) {
            sumEditUsers.addAll(editUsers);
        }
        return sumEditUsers.stream().distinct().collect(Collectors.toList());
    }
}
