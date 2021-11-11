package com.webank.wedatasphere.warehouse.restful;

import com.webank.wedatasphere.linkis.server.Message;
import com.webank.wedatasphere.warehouse.cqe.DwLayerCreateCommand;
import com.webank.wedatasphere.warehouse.cqe.DwLayerQueryCommand;
import com.webank.wedatasphere.warehouse.cqe.DwLayerUpdateCommand;
import com.webank.wedatasphere.warehouse.exception.DwException;
import com.webank.wedatasphere.warehouse.service.DwLayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Component
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/data-warehouse")
public class DwLayerRestfulApi {

    private final DwLayerService dwLayerService;

    @Autowired
    public DwLayerRestfulApi(DwLayerService dwLayerService) {
        this.dwLayerService = dwLayerService;
    }

    // list all preset layers
    @GET
    @Path("/layers/preset")
    public Response getAllPresetLayers(@Context HttpServletRequest request) throws DwException {
        Message message = this.dwLayerService.getAllPresetLayers(request);
        return Message.messageToResponse(message);
    }

    @GET
    @Path("/layers/all")
    public Response getAllLayers(@Context HttpServletRequest request, @QueryParam(value = "isAvailable") Boolean isAvailable) throws DwException {
        Message message = this.dwLayerService.getAllLayers(request, isAvailable);
        return Message.messageToResponse(message);
    }

    // query paged custom layers
    @GET
    @Path("/layers/custom")
    public Response queryPagedCustomLayers(
            @Context HttpServletRequest request,
            @QueryParam("page") Integer page,
            @QueryParam("size") Integer size,
            @QueryParam("name") String name,
            @QueryParam("enabled") Boolean enabled
    )throws DwException {
        final DwLayerQueryCommand command = new DwLayerQueryCommand();
        command.setName(name);
        command.setEnabled(enabled);
        command.setPage(page);
        command.setSize(size);
        Message message = this.dwLayerService.queryPagedCustomLayers(request, command);
        return Message.messageToResponse(message);
    }

    // create custom layer
    @POST
    @Path("/layers/custom")
    public Response createDwCustomLayer(@Context HttpServletRequest request, DwLayerCreateCommand command) throws DwException {
        Message message = this.dwLayerService.createDwCustomLayer(request, command);
        return Message.messageToResponse(message);
    }

    // get layer by id
    @GET
    @Path("/layers/{id}")
    public Response getLayerById(
            @Context HttpServletRequest request,
            @PathParam("id") Long id
    ) throws DwException {
        Message message = this.dwLayerService.getLayerById(request, id);
        return Message.messageToResponse(message);
    }

    // delete layer
    @DELETE
    @Path("/layers/{id}")
    public Response deleteById(
            @Context HttpServletRequest request,
            @PathParam("id") Long id
    ) throws DwException {
        Message message = this.dwLayerService.deleteById(request, id);
        return Message.messageToResponse(message);
    }

    // update layer
    @PUT
    @Path("/layers/{id}")
    public Response update(
            @Context HttpServletRequest request,
            @PathParam("id") Long id,
            DwLayerUpdateCommand command
    ) throws DwException {
        command.setId(id);
        Message message = this.dwLayerService.update(request, command);
        return Message.messageToResponse(message);
    }

    // enable layer
    @PUT
    @Path("/layers/{id}/enable")
    public Response enable(
            @Context HttpServletRequest request,
            @PathParam("id") Long id
    ) throws DwException {
        Message message = this.dwLayerService.enable(request, id);
        return Message.messageToResponse(message);
    }

    // disable layer
    @PUT
    @Path("/layers/{id}/disable")
    public Response disable(
            @Context HttpServletRequest request,
            @PathParam("id") Long id
    ) throws DwException {
        Message message = this.dwLayerService.disable(request, id);
        return Message.messageToResponse(message);
    }
}
