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

package com.webank.wedatasphere.dss.framework.project.restful;

import com.google.gson.*;
import com.webank.wedatasphere.dss.framework.project.conf.ProjectConf;
import com.webank.wedatasphere.dss.framework.project.utils.DateUtil;
import com.webank.wedatasphere.dss.framework.project.utils.HttpClientUtil;
import com.webank.wedatasphere.dss.framework.project.utils.RestfulUtils;
import com.webank.wedatasphere.linkis.server.Message;
import com.webank.wedatasphere.linkis.server.security.SecurityFilter;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;

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
    private static final Logger LOG = LoggerFactory.getLogger(DSSFrameworkDSTokenRestfulApi.class);

    @GET
    @Path("/ds/token")
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
    }
}
