package com.webank.wedatasphere.warehouse.restful;

import org.apache.linkis.server.Message;
import com.webank.wedatasphere.warehouse.cqe.*;
import com.webank.wedatasphere.warehouse.exception.DwException;
import com.webank.wedatasphere.warehouse.service.DwThemeDomainService;
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
public class DwThemeDomainRestfulApi {

    private final DwThemeDomainService dwThemeDomainService;

    @Autowired
    public DwThemeDomainRestfulApi(DwThemeDomainService dwThemeDomainService) {
        this.dwThemeDomainService = dwThemeDomainService;
    }

    // query paged theme domains
    @GET
    @Path("/themedomains")
    public Response queryPagedCustomLayers(
            @Context HttpServletRequest request,
            @QueryParam("page") Integer page,
            @QueryParam("size") Integer size,
            @QueryParam("name") String name,
            @QueryParam("enabled") Boolean enabled
    )throws DwException {
        final DwThemeDomainQueryCommand command = new DwThemeDomainQueryCommand();
        command.setName(name);
        command.setPage(page);
        command.setSize(size);
        command.setEnabled(enabled);
        Message message = this.dwThemeDomainService.queryPage(request, command);
        return Message.messageToResponse(message);
    }

    @GET
    @Path("/themedomains/all")
    public Response queryAllThemeDomains(
            @Context HttpServletRequest request,
            @QueryParam("name") String name,
            @QueryParam(value = "isAvailable") Boolean isAvailable
    )throws DwException {
        final DwThemeDomainQueryCommand command = new DwThemeDomainQueryCommand();
        command.setName(name);
        command.setEnabled(isAvailable);
        Message message = this.dwThemeDomainService.queryAllThemeDomains(request, command);
        return Message.messageToResponse(message);
    }

    // create theme domain
    @POST
    @Path("/themedomains")
    public Response createDwCustomLayer(@Context HttpServletRequest request, DwThemeDomainCreateCommand command) throws DwException {
        Message message = this.dwThemeDomainService.create(request, command);
        return Message.messageToResponse(message);
    }

    // get theme domain by id
    @GET
    @Path("/themedomains/{id}")
    public Response getLayerById(
            @Context HttpServletRequest request,
            @PathParam("id") Long id
    ) throws DwException {
        Message message = this.dwThemeDomainService.getById(request, id);
        return Message.messageToResponse(message);
    }

    // delete theme domain
    @DELETE
    @Path("/themedomains/{id}")
    public Response deleteById(
            @Context HttpServletRequest request,
            @PathParam("id") Long id
    ) throws DwException {
        Message message = this.dwThemeDomainService.deleteById(request, id);
        return Message.messageToResponse(message);
    }

    // update theme domain
    @PUT
    @Path("/themedomains/{id}")
    public Response update(
            @Context HttpServletRequest request,
            @PathParam("id") Long id,
            DwThemeDomainUpdateCommand command
    ) throws DwException {
        command.setId(id);
        Message message = this.dwThemeDomainService.update(request, command);
        return Message.messageToResponse(message);
    }

    // enable theme domain
    @PUT
    @Path("/themedomains/{id}/enable")
    public Response enable(
            @Context HttpServletRequest request,
            @PathParam("id") Long id
    ) throws DwException {
        Message message = this.dwThemeDomainService.enable(request, id);
        return Message.messageToResponse(message);
    }

    // disable theme domain
    @PUT
    @Path("/themedomains/{id}/disable")
    public Response disable(
            @Context HttpServletRequest request,
            @PathParam("id") Long id
    ) throws DwException {
        Message message = this.dwThemeDomainService.disable(request, id);
        return Message.messageToResponse(message);
    }
}
