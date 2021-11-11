package com.webank.wedatasphere.warehouse.restful;

import com.google.common.base.Strings;
import com.webank.wedatasphere.linkis.server.Message;
import com.webank.wedatasphere.warehouse.cqe.*;
import com.webank.wedatasphere.warehouse.exception.DwException;
import com.webank.wedatasphere.warehouse.service.DwStatisticalPeriodService;
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
public class DwStatisticalPeriodRestfulApi {

    private final DwStatisticalPeriodService dwStatisticalPeriodService;

    @Autowired
    public DwStatisticalPeriodRestfulApi(final DwStatisticalPeriodService dwStatisticalPeriodService) {
        this.dwStatisticalPeriodService = dwStatisticalPeriodService;
    }

    @GET
    @Path("/statistical_periods/all")
    public Response queryAll(
            @Context HttpServletRequest request,
            @QueryParam("name") String name,
            @QueryParam(value = "isAvailable") Boolean isAvailable
    )throws DwException {
        final DwStatisticalPeriodQueryCommand command = new DwStatisticalPeriodQueryCommand();
        command.setName(name);
        command.setEnabled(isAvailable);
        Message message = this.dwStatisticalPeriodService.queryAll(request, command);
        return Message.messageToResponse(message);
    }

    // query paged statistical periods
    @GET
    @Path("/statistical_periods")
    public Response queryPagedDecorations(
            @Context HttpServletRequest request,
            @QueryParam("page") Integer page,
            @QueryParam("size") Integer size,
            @QueryParam("name") String name,
            @QueryParam(value = "enabled") String enabled
    )throws DwException {
        final DwStatisticalPeriodQueryCommand command = new DwStatisticalPeriodQueryCommand();
        command.setName(name);
        command.setPage(page);
        command.setSize(size);
        if (!Strings.isNullOrEmpty(enabled)) {
            command.setEnabled(Boolean.parseBoolean(enabled));
        }
        Message message = this.dwStatisticalPeriodService.queryPage(request, command);
        return Message.messageToResponse(message);
    }

    // create dw statistical_periods
    @POST
    @Path("/statistical_periods")
    public Response create(@Context HttpServletRequest request, DwStatisticalPeriodCreateCommand command) throws DwException {
        Message message = this.dwStatisticalPeriodService.create(request, command);
        return Message.messageToResponse(message);
    }

    // fetch one statistical_periods details by id
    @GET
    @Path("/statistical_periods/{id}")
    public Response getById(
            @Context HttpServletRequest request,
            @PathParam("id") Long id
    ) throws DwException {
        Message message = this.dwStatisticalPeriodService.getById(request, id);
        return Message.messageToResponse(message);
    }

    // remove statistical_periods logic
    @DELETE
    @Path("/statistical_periods/{id}")
    public Response deleteById(
            @Context HttpServletRequest request,
            @PathParam("id") Long id
    ) throws DwException {
        Message message = this.dwStatisticalPeriodService.deleteById(request, id);
        return Message.messageToResponse(message);
    }

    // update statistical_periods
    @PUT
    @Path("/statistical_periods/{id}")
    public Response update(
            @Context HttpServletRequest request,
            @PathParam("id") Long id,
            DwStatisticalPeriodUpdateCommand command
    ) throws DwException {
        command.setId(id);
        Message message = this.dwStatisticalPeriodService.update(request, command);
        return Message.messageToResponse(message);
    }


    // enable statistical_periods
    @PUT
    @Path("/statistical_periods/{id}/enable")
    public Response enable(
            @Context HttpServletRequest request,
            @PathParam("id") Long id
    ) throws DwException {
        Message message = this.dwStatisticalPeriodService.enable(request, id);
        return Message.messageToResponse(message);
    }

    // disable statistical_periods
    @PUT
    @Path("/statistical_periods/{id}/disable")
    public Response disable(
            @Context HttpServletRequest request,
            @PathParam("id") Long id
    ) throws DwException {
        Message message = this.dwStatisticalPeriodService.disable(request, id);
        return Message.messageToResponse(message);
    }
}
