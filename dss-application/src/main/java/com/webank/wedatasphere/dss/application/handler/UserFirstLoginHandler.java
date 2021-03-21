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

package com.webank.wedatasphere.dss.application.handler;

import com.webank.wedatasphere.dss.application.entity.DSSUser;
import com.webank.wedatasphere.dss.application.service.DSSApplicationUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by chaogefeng on 2019/10/15.
 */
@Component
public class UserFirstLoginHandler implements Handler {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DSSApplicationUserService dssApplicationUserService;

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public void handle(DSSUser user) {
        logger.info("UserFirstLoginHandler:");
        synchronized (user.getUsername().intern()){
            DSSUser userDb = dssApplicationUserService.getUserByName(user.getUsername());
            if(userDb == null){
                logger.info("User first enter dss, insert table dss_user");
                userDb = new DSSUser();
                userDb.setUsername(user.getUsername());
                userDb.setName(user.getName());
                userDb.setFirstLogin(true);
                userDb.setId(user.getId());
                dssApplicationUserService.registerDssUser(userDb);
            }
            // TODO: 2019/11/29 update  firstLogin
            user = userDb;
        }

    }
}
