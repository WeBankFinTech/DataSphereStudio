

package com.webank.wedatasphere.dss.framework.project.restful;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.webank.wedatasphere.dss.appconn.manager.AppConnManager;
import com.webank.wedatasphere.dss.appconn.scheduler.SchedulerAppConn;
import com.webank.wedatasphere.dss.framework.project.contant.DSSProjectConstant;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefQueryOperation;
import com.webank.wedatasphere.dss.standard.app.development.ref.CommonRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.ref.impl.CommonRequestRefImpl;
import com.webank.wedatasphere.dss.standard.app.development.service.RefQueryService;
import com.webank.wedatasphere.dss.standard.common.desc.AppInstance;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.dss.workflow.conversion.WorkflowConversionIntegrationStandard;
import org.apache.linkis.server.Message;
import org.apache.linkis.server.security.SecurityFilter;

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

    @GET
    @Path("/ds/token")
    public Response dsApiServiceTokenCreate(@Context HttpServletRequest req) {
        String userName = SecurityFilter.getLoginUsername(req);

        SchedulerAppConn schedulerAppConn = (SchedulerAppConn)AppConnManager.getAppConnManager()
            .getAppConn(DSSProjectConstant.DSS_SCHEDULER_APPCONN_NAME.getValue());
        if (schedulerAppConn == null) {
            logger.error("dolphinscheduler appconn is null, can not get scheduler api access token");
            return Message.messageToResponse(Message.error("dolphinscheduler appconn is null"));
        }

        WorkflowConversionIntegrationStandard standard = schedulerAppConn.getOrCreateWorkflowConversionStandard();
        AppInstance schedulerInstance = schedulerAppConn.getAppDesc().getAppInstances().get(0);
        RefQueryService tokenQueryService = standard.getQueryService(schedulerInstance);
        RefQueryOperation tokenRefQueryOperation = tokenQueryService.getRefQueryOperation();

        CommonRequestRef requestRef = new CommonRequestRefImpl();
        requestRef.setParameter("userName", userName);
        try {
            ResponseRef responseRef = tokenRefQueryOperation.query(requestRef);
            return Message.messageToResponse(Message.ok().data("token", responseRef.getValue("token"))
                .data("expire_time", Long.valueOf((String)responseRef.getValue("expire_time"))));
        } catch (ExternalOperationFailedException e) {
            return Message.messageToResponse(Message.error("获取token失败:" + e.getMessage()));
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
