package com.webank.wedatasphere.dss.server.restful;

import com.webank.wedatasphere.linkis.server.Message;
import com.webank.wedatasphpere.dss.user.dto.request.AuthorizationBody;
import com.webank.wedatasphpere.dss.user.service.impl.LubanAuthorizationClient;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @program: luban-authorization
 * @description: 鲁班对外交互的接口  包括施工 注册用户
 * @author: luxl@chinatelecom.cn
 * @create: 2020-08-12 14:24
 **/

@Component
@Path("/dss")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserManagerApi {

    private ExecutorService executor = Executors.newCachedThreadPool();
    private LubanAuthorizationClient client = new LubanAuthorizationClient();

    @POST
    @Path("/user")
    public Response createUser(@Context HttpServletRequest req, AuthorizationBody body) {

        final Future<String> futureRate = executor.submit(new Callable<String>() {
            public String call() {
                //以异步处理
                return client.authorization(body);
            }
        });

        return Message.messageToResponse(Message.ok());
    }


    @GET
    @Path("/paths")
    public Response getPaths(@Context HttpServletRequest req) {
        List<String> paths = new ArrayList<>();
        paths.add("hdfs:///dss_workspace/linkis");
        return Message.messageToResponse(Message.ok().data("paths", paths));
    }
}
