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

package com.webank.wedatasphere.dss.apiservice.core.token;

import com.webank.wedatasphere.dss.apiservice.core.dao.ApiServiceTokenManagerDao;
import com.webank.wedatasphere.dss.apiservice.core.util.DateUtil;
import com.webank.wedatasphere.dss.apiservice.core.vo.TokenManagerVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Component
@EnableScheduling
public class TokenStatusCheckerTask {
    private static final Logger LOG = LoggerFactory.getLogger(TokenStatusCheckerTask.class);

    @Autowired
    ApiServiceTokenManagerDao atmd;

    @Scheduled(cron = "0 0/10 * * * ?")
    public void doTokenStatusCheckTask() {
        // 查询启用状态的token
        List<TokenManagerVo> tokenManagerVos = atmd.queryTokenByStatus(1);
        if (null != tokenManagerVos) {
            for (TokenManagerVo tmv : tokenManagerVos) {
                Date applyTime = tmv.getApplyTime();
                Date nowTime = Calendar.getInstance().getTime();
                Calendar cal = Calendar.getInstance();
                cal.setTime(applyTime);
                cal.add(Calendar.DATE, tmv.getDuration().intValue());
                Date endTime = cal.getTime();
                if (endTime.compareTo(nowTime) < 0) {
                    LOG.warn("token id:" + tmv.getId() + " 已经过期！");
                    atmd.disableTokenStatus(tmv.getId());
                }
            }
        }
    }

}
