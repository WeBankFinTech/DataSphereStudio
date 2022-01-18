package com.webank.wedatasphere.warehouse.restful;

import com.google.common.base.Strings;
import org.apache.linkis.server.Message;
import com.webank.wedatasphere.warehouse.cqe.*;
import com.webank.wedatasphere.warehouse.exception.DwException;
import com.webank.wedatasphere.warehouse.service.DwModifierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Optional;

@Component
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/data-warehouse")
public class DwModifierRestfulApi {

    private final DwModifierService dwModifierService;

    @Autowired
    public DwModifierRestfulApi(DwModifierService dwModifierService) {
        this.dwModifierService = dwModifierService;
    }

    @GET
    @Path("/modifiers/all")
    public Response queryAllModifiers(
            @Context HttpServletRequest request,
            @QueryParam("typeName") String typeName,
            @QueryParam(value = "isAvailable") Boolean isAvailable,
            @QueryParam(value = "theme") String theme,
            @QueryParam(value = "layer") String layer
    )throws DwException {
        final DwModifierQueryCommand command = new DwModifierQueryCommand();
        command.setName(typeName);
        command.setEnabled(isAvailable);
        command.setTheme(theme);
        command.setLayer(layer);
        Message message = this.dwModifierService.queryAllModifiers(request, command);
        return Message.messageToResponse(message);
    }

    // query paged modifiers
    @GET
    @Path("/modifiers")
    public Response queryPagedDecorations(
            @Context HttpServletRequest request,
            @QueryParam("page") Integer page,
            @QueryParam("size") Integer size,
            @QueryParam("name") String typeName,
            @QueryParam("enabled") String enabled
    )throws DwException {
        final DwModifierQueryCommand command = new DwModifierQueryCommand();
        command.setName(typeName);
        command.setPage(page);
        command.setSize(size);
        if (!Strings.isNullOrEmpty(enabled)) {
            command.setEnabled(Boolean.parseBoolean(enabled));
        }
        Message message = this.dwModifierService.queryPage(request, command);
        return Message.messageToResponse(message);
    }

    // create dw decoration word
    @POST
    @Path("/modifiers")
    public Response create(@Context HttpServletRequest request, DwModifierCreateCommand command) throws DwException {
        Message message = this.dwModifierService.create(request, command);
        return Message.messageToResponse(message);
    }

    // fetch one decoration details by id
    @GET
    @Path("/modifiers/{id}")
    public Response getById(
            @Context HttpServletRequest request,
            @PathParam("id") Long id
    ) throws DwException {
        Message message = this.dwModifierService.getById(request, id);
        return Message.messageToResponse(message);
    }

    // remove decoration logic
    @DELETE
    @Path("/modifiers/{id}")
    public Response deleteById(
            @Context HttpServletRequest request,
            @PathParam("id") Long id
    ) throws DwException {
        Message message = this.dwModifierService.deleteById(request, id);
        return Message.messageToResponse(message);
    }

    // update
    @PUT
    @Path("/modifiers/{id}")
    public Response update(
            @Context HttpServletRequest request,
            @PathParam("id") Long id,
            DwModifierUpdateCommand command
    ) throws DwException {
        command.setId(id);
        Message message = this.dwModifierService.update(request, command);
        return Message.messageToResponse(message);
    }

    // enable modifier
    @PUT
    @Path("/modifiers/{id}/enable")
    public Response enable(
            @Context HttpServletRequest request,
            @PathParam("id") Long id
    ) throws DwException {
        Message message = this.dwModifierService.enable(request, id);
        return Message.messageToResponse(message);
    }

    // disable modifier
    @PUT
    @Path("/modifiers/{id}/disable")
    public Response disable(
            @Context HttpServletRequest request,
            @PathParam("id") Long id
    ) throws DwException {
        Message message = this.dwModifierService.disable(request, id);
        return Message.messageToResponse(message);
    }
}
