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

package com.webank.wedatasphere.dss.apiservice.core.restful;

import com.github.pagehelper.PageInfo;
import com.webank.wedatasphere.dss.apiservice.core.constant.SaveTokenEnum;
import com.webank.wedatasphere.dss.apiservice.core.datamap.DataMapStatus;
import com.webank.wedatasphere.dss.apiservice.core.service.ApprovalService;
import com.webank.wedatasphere.dss.apiservice.core.token.TokenAuth;
import com.webank.wedatasphere.dss.apiservice.core.service.TokenQueryService;
import com.webank.wedatasphere.dss.apiservice.core.util.ApiUtils;
import com.webank.wedatasphere.dss.apiservice.core.util.DateUtil;
import com.webank.wedatasphere.dss.apiservice.core.vo.ApprovalVo;
import com.webank.wedatasphere.dss.apiservice.core.vo.TokenManagerVo;
import com.webank.wedatasphere.dss.apiservice.core.bo.TokenQuery;
import com.webank.wedatasphere.linkis.server.Message;
import com.webank.wedatasphere.linkis.server.security.SecurityFilter;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author allenlliu
 * @version 2.0.0
 * @date 2020/08/17 05:37 PM
 */

@Path("/dss/apiservice")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Component
public class ApiServiceTokenRestfulApi {
    private static final Logger LOG = LoggerFactory.getLogger(ApiServiceTokenRestfulApi.class);

    @Autowired
    TokenQueryService tokenQueryService;

    @Autowired
    ApprovalService approvalService;

    @Autowired
    TokenAuth tokenAuth;


    @GET
    @Path("/tokenQuery")
    public Response apiServiceTokenQuery(@QueryParam("apiId") Long apiId,
                                         @QueryParam("user") String user,
                                         @QueryParam("status") Integer status,
                                         @QueryParam("startDate") String startDateStr,
                                         @QueryParam("endDate") String endDateStr,
                                         @QueryParam("currentPage") Integer currentPage,
                                         @QueryParam("pageSize") Integer pageSize,
                                         @Context HttpServletRequest req) {
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


    @GET
    @Path("/approvalRefresh")
    public Response refresh(@QueryParam("approvalNo") String approvalNo,
                            @Context HttpServletRequest req) {
        String userName = SecurityFilter.getLoginUsername(req);
        return ApiUtils.doAndResponse(() ->{

            ApprovalVo approvalVo = approvalService.refreshStatus(approvalNo);
            if(approvalVo.getCreator().equals(userName) == false){
                return Message.error("'api service user check failed' [用户检查失败！]");
            }
            if(null == approvalVo) {
                return Message.error("'api service approvalNo' is missing[未查询到对应的审批单号]");
            }
            if(approvalVo.getStatus().equals(DataMapStatus.SUCCESS.getIndex())) {
                List<TokenManagerVo> tokenManagerVoList = tokenAuth.genTokenRecord(approvalVo);

                SaveTokenEnum res = tokenAuth.saveTokensToDb(tokenManagerVoList, approvalNo);
                if(SaveTokenEnum.FAILED == res) {
                    return Message.error("'token auth process occur error' [token认证过程出现错误]");
                }
            }
            String approvalStatusDesc = DataMapStatus.getDescByIndex(approvalVo.getStatus());
            return Message.ok().data("approvalStatus", approvalStatusDesc);
        }, "/apiservice/approvalRefresh/",  "提交审批单查询出错");
    }
}
