

package com.webank.wedatasphere.dss.framework.project.restful;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.webank.wedatasphere.dss.appconn.schedule.core.SchedulerAppConn;
import com.webank.wedatasphere.dss.appconn.schedule.core.standard.SchedulerStructureStandard;
import com.webank.wedatasphere.dss.framework.appconn.service.AppConnService;
import com.webank.wedatasphere.dss.framework.project.conf.ProjectConf;
import com.webank.wedatasphere.dss.framework.project.utils.DateUtil;
import com.webank.wedatasphere.dss.framework.project.utils.HttpClientUtil;
import com.webank.wedatasphere.dss.framework.project.utils.RestfulUtils;
import com.webank.wedatasphere.dss.framework.release.utils.ReleaseConf;
import com.webank.wedatasphere.dss.standard.app.development.crud.CommonRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.query.RefQueryOperation;
import com.webank.wedatasphere.dss.standard.app.development.query.RefQueryService;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.linkis.server.Message;
import com.webank.wedatasphere.linkis.server.security.SecurityFilter;

/**
 * @author allenlliu
 * @version 2.0.0
 * @date 2020/08/17 05:37 PM
 */

@Path("/dss/framework/project")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Component
public class DSSFrameworkDSTokenRestfulApi {
    private static final Logger logger = LoggerFactory.getLogger(DSSFrameworkDSTokenRestfulApi.class);

    @Autowired
    private AppConnService appConnService;

    private SchedulerAppConn schedulerAppConn;

    @PostConstruct
    public void init() {
        schedulerAppConn =
            (SchedulerAppConn)appConnService.getAppConn(ReleaseConf.DSS_SCHEDULE_APPCONN_NAME.getValue());
    }

    @GET
    @Path("/ds/token")
    public Response dsApiServiceTokenCreate(@Context HttpServletRequest req) {
        String userName = SecurityFilter.getLoginUsername(req);

        if (schedulerAppConn == null) {
            logger.error("scheduler appconn is null, can not get scheduler api access token");
            return RestfulUtils.dealError("scheduler appconn is null");
        }

        SchedulerStructureStandard schedulerStructureStandard =
            (SchedulerStructureStandard)schedulerAppConn.getAppStandards().stream()
                .filter(appStandard -> appStandard instanceof SchedulerStructureStandard).findAny().orElse(null);
        if (schedulerStructureStandard == null) {
            logger.error("scheduler structure standard is null, can not continue");
            return RestfulUtils.dealError("scheduler Structure Standard is null");
        }

        RefQueryService tokenQueryService = schedulerStructureStandard.getQueryService();
        RefQueryOperation tokenRefQueryOperation = tokenQueryService.getRefQueryOperation();
        if (tokenRefQueryOperation == null) {
            logger.error("scheduler token query operation is null, can not continue");
            return RestfulUtils.dealError("scheduler token query operation is null");
        }

        CommonRequestRef requestRef = new CommonRequestRef();
        requestRef.setParameter("userName", userName);
        try {
            ResponseRef responseRef = tokenRefQueryOperation.query(requestRef);
            return Message.messageToResponse(Message.ok().data("token", responseRef.getValue("token"))
                .data("expire_time", Long.valueOf((String)responseRef.getValue("expire_time"))));
        } catch (ExternalOperationFailedException e) {
            return RestfulUtils.dealError("获取token失败:" + e.getMessage());
        }
    }

   /* @GET
    @Path("/ds/token1")
    public Response apiServiceTokenQuery(
                                         @Context HttpServletRequest req) {


            String userId = SecurityFilter.getLoginUsername(req);

            try{
                String dsUrl = ProjectConf.DS_URL.getValue();
                Map<String, Object> headerMap = new HashMap();
                headerMap.put("token", ProjectConf.DS_ADMIN_TOKEN.getValue());
                String userExistsRes = HttpClientUtil.executeGetByHeader(dsUrl+"/users/verify-user-name?userName=" + userId, headerMap);

                JsonObject userExistJSON =  new JsonParser().parse(userExistsRes).getAsJsonObject();
                String userExistCode = userExistJSON.getAsJsonObject().get("code").getAsString();
                String userExistMsg =   userExistJSON.getAsJsonObject().get("msg").getAsString();

                if("0".equals(userExistCode) && "success".equals(userExistMsg)){
                    List<NameValuePair> userRequestParas = new ArrayList<NameValuePair>();
                    userRequestParas.add(new BasicNameValuePair("userName", userId));
                    userRequestParas.add(new BasicNameValuePair("userPassword", "rz3Lw7yFlKv@8GisM"));
                    userRequestParas.add(new BasicNameValuePair("tenantId", "1"));
                    userRequestParas.add(new BasicNameValuePair("email", "xx@qq.com"));
                    userRequestParas.add(new BasicNameValuePair("queue", "default"));
                    HttpClientUtil.postForm(dsUrl+"/users/create", 30000, headerMap, userRequestParas, "UTF-8");
                }

                String userListRes = HttpClientUtil.executeGetByHeader(dsUrl+"/users/list-paging?pageNo=1&pageSize=10&searchVal=" + userId, headerMap);
                String  id = "";
                JsonArray userListJSON = new JsonParser().parse(userListRes).getAsJsonObject().get("data").getAsJsonObject().getAsJsonArray("totalList");

                for(JsonElement jsonElement:userListJSON){
                    String jsonUserName = jsonElement.getAsJsonObject().get("userName").getAsString();
                    if(userId.equals(jsonUserName)){
                        id = jsonElement.getAsJsonObject().get("id").getAsString();
                        break;
                    }
                }

                String expireTime = DateUtil.addHours(new Date(), 2);

                List<NameValuePair> tokenGenerateRequestParas = new ArrayList<NameValuePair>();
                tokenGenerateRequestParas.add(new BasicNameValuePair("userId", id));
                tokenGenerateRequestParas.add(new BasicNameValuePair("expireTime", expireTime));
                String tokenGenerateRes = HttpClientUtil.postForm(dsUrl+"/access-token/generate", 30000, headerMap, tokenGenerateRequestParas, "UTF-8");
                JsonObject tokenGenerateJSON =  new JsonParser().parse(tokenGenerateRes).getAsJsonObject();
                String token = tokenGenerateJSON.get("data").getAsString();

                List<NameValuePair> tokenCreateRequestParas = new ArrayList<NameValuePair>();
                tokenCreateRequestParas.add(new BasicNameValuePair("userId", id));
                tokenCreateRequestParas.add(new BasicNameValuePair("expireTime", expireTime));
                tokenCreateRequestParas.add(new BasicNameValuePair("token", token));
                HttpClientUtil.postForm(dsUrl+"/access-token/create",30000,headerMap,tokenCreateRequestParas,"UTF-8");
                long expireTimeStamp = DateUtil.strToTimeStamp(expireTime,DateUtil.FORMAT_LONG)-30000;
                return Message.messageToResponse(Message.ok().data("token", token).data("expire_time",expireTimeStamp));

            }catch (Exception exception){
                return RestfulUtils.dealError("获取token失败:" + exception.getMessage());
            }
    }*/
}
