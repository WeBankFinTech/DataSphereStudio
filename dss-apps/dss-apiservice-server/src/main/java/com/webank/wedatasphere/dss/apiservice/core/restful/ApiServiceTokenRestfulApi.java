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

package com.webank.wedatasphere.dss.apiservice.core.restful;

import com.github.pagehelper.PageInfo;
import com.webank.wedatasphere.dss.apiservice.core.bo.TokenQuery;
import com.webank.wedatasphere.dss.apiservice.core.service.TokenQueryService;
import com.webank.wedatasphere.dss.apiservice.core.token.TokenAuth;
import com.webank.wedatasphere.dss.apiservice.core.util.ApiUtils;
import com.webank.wedatasphere.dss.apiservice.core.util.DateUtil;
import com.webank.wedatasphere.dss.apiservice.core.vo.TokenManagerVo;
import org.apache.commons.lang.StringUtils;
import org.apache.linkis.server.Message;
import org.apache.linkis.server.security.SecurityFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Calendar;
import java.util.Date;



@RequestMapping(path = "/dss/apiservice", produces = {"application/json"})
@RestController
public class ApiServiceTokenRestfulApi {
    private static final Logger LOG = LoggerFactory.getLogger(ApiServiceTokenRestfulApi.class);
    @Autowired
    TokenQueryService tokenQueryService;
    @Autowired
    TokenAuth tokenAuth;

    @RequestMapping(value = "/tokenQuery",method = RequestMethod.GET)
    public Message apiServiceTokenQuery(@RequestParam(required = false, name = "apiId") Long apiId,
                                         @RequestParam(required = false, name = "user") String user,
                                         @RequestParam(required = false, name = "status") Integer status,
                                         @RequestParam(required = false, name = "startDate") String startDateStr,
                                         @RequestParam(required = false, name = "endDate") String endDateStr,
                                         @RequestParam(required = false, name = "currentPage") Integer currentPage,
                                         @RequestParam(required = false, name = "pageSize") Integer pageSize,
                                         HttpServletRequest req) {
        String userName = SecurityFilter.getLoginUsername(req);
        return ApiUtils.doAndResponse(() -> {

            TokenQuery query = null;

            if(StringUtils.isBlank(startDateStr) || StringUtils.isBlank(endDateStr)) {
                query = new TokenQuery(apiId, user, status);
            } else {
                Long startDateSecond = Long.parseLong(startDateStr);
                Long endDateSeconed = Long.parseLong(endDateStr);
                String startDate = DateUtil.format(new Date(startDateSecond), DateUtil.FORMAT_LONG);
                Date endDateReal = new Date(endDateSeconed);
                Calendar cal = Calendar.getInstance();
                cal.setTime(endDateReal);
                cal.add(Calendar.DATE, 1);
                Date realDate = cal.getTime();
                String endDate = DateUtil.format(realDate, DateUtil.FORMAT_LONG);

                query = new TokenQuery(apiId, user, status, startDate, endDate);

            }
            query.setCreator(userName);
            query.setCurrentPage(null != currentPage ? currentPage : 1);
            query.setPageSize(null != pageSize ? pageSize : 10);
            PageInfo<TokenManagerVo> tokenList = tokenQueryService.query(query);
            return Message.ok().data("queryList", tokenList.getList()).data("total", tokenList.getTotal());
        }, "/apiservice/tokenQuery", "Fail to query page of token[查询token信息失败]");
    }


    @RequestMapping(value = "/approvalRefresh",method = RequestMethod.GET)
    public Message refresh(@RequestParam(required = false, name = "approvalNo") String approvalNo,
                            HttpServletRequest req) {
        String userName = SecurityFilter.getLoginUsername(req);
        return ApiUtils.doAndResponse(() ->{
            return Message.ok().data("approvalStatus", "success");
        }, "/apiservice/approvalRefresh/",  "查询出错");
    }
}
