package com.webank.wedatasphere.warehouse.restful;

import com.webank.wedatasphere.linkis.server.Message;
import com.webank.wedatasphere.warehouse.service.DwDsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Component
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/data-warehouse")
public class DwDsRestfulApi {

    private final DwDsService dwDsService;

    @Autowired
    public DwDsRestfulApi(DwDsService dwDsService) {
        this.dwDsService = dwDsService;
    }

    // list all hive dbs
    @GET
    @Path("/dbs/hive")
    public Response getAllHiveDbs(@Context HttpServletRequest request) throws Exception {
        Message message = this.dwDsService.getAllHiveDbs(request);
        return Message.messageToResponse(message);
    }

}
