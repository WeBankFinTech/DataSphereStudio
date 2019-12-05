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
import com.webank.wedatasphere.dss.application.entity.LinkisUser;
import com.webank.wedatasphere.dss.application.service.LinkisUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Created by chaogefeng on 2019/11/29.
 */
@Component
public class LinkisUserFirstLoginHandler implements Handler{

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private LinkisUserService linkisUserService;

    @Override
    public int getOrder() {
        return -1;
    }

    @Override
    public void handle(DSSUser user) {
        logger.info("LinkisUserFirstLoginHandler:");
        synchronized (user.getUsername().intern()){
            LinkisUser userDb = linkisUserService.getUserByName(user.getUsername());
            if(userDb == null){
                logger.info("User first enter linkis, insert table linkis_user and dss_user");
                userDb = new LinkisUser();
                userDb.setUserName(user.getUsername());
                userDb.setName(user.getName());
                userDb.setFirstLogin(true);
                userDb.setCreateTime(new Date());
                userDb.setUpdateTime(new Date());
                linkisUserService.registerLinkisAndDssUser(userDb);
            }
            user.setId(userDb.getId());
        }
    }
}
