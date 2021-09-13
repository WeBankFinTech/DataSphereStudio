package com.webank.wedatasphere.dss.datamodel.dimension.restful;

import com.webank.wedatasphere.linkis.server.Message;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

@Component
@Path("/datamodel/config")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ConfigurationRestfulApi {

    @GET
    @Path("/view")
    public Response getView(@Context HttpServletRequest req) throws IOException {
        return  Message.messageToResponse(Message.ok().data("fullTree","test"));
    }
}
