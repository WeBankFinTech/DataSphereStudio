

package com.webank.wedatasphere.dss.framework.dbapi.restful;

import com.webank.wedatasphere.linkis.server.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @author allenlliu
 * @version 2.0.0
 * @date 2020/08/17 05:37 PM
 */

@Path("/dss/framework/project")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Component
public class Demo {
    private static final Logger logger = LoggerFactory.getLogger(Demo.class);



    @GET
    @Path("/ds/token2")
    public Response apiServiceTokenQuery(
                                         @Context HttpServletRequest req) {


                return Message.messageToResponse(Message.ok().data("token", 11).data("expire_time",111));


    }
}
