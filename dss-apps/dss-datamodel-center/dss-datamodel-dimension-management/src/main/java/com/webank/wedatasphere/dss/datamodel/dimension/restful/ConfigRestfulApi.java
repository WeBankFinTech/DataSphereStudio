package com.webank.wedatasphere.dss.datamodel.dimension.restful;

import com.webank.wedatasphere.linkis.server.Message;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

/**
 * @author Dylan
 * @date 2021/9/14
 */
@Component
@Path("/datamodel/config")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ConfigRestfulApi {

    @GET
    @Path("/get")
    public Response getView(@Context HttpServletRequest req) throws IOException {
        return  Message.messageToResponse(Message.ok().data("fullTree","get"));
    }
}
