package com.webank.wedatasphere.warehouse.restful;

import com.google.common.base.Strings;
import org.apache.linkis.server.Message;
import com.webank.wedatasphere.warehouse.cqe.*;
import com.webank.wedatasphere.warehouse.exception.DwException;
import com.webank.wedatasphere.warehouse.service.DwStatisticalPeriodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;


@RestController
@RequestMapping(value = "/data-warehouse", produces = {"application/json;charset=utf-8"})
public class DwStatisticalPeriodRestfulApi {

    private final DwStatisticalPeriodService dwStatisticalPeriodService;

    @Autowired
    public DwStatisticalPeriodRestfulApi(final DwStatisticalPeriodService dwStatisticalPeriodService) {
        this.dwStatisticalPeriodService = dwStatisticalPeriodService;
    }


    @RequestMapping( value = "/statistical_periods/all", method = RequestMethod.GET)
    public Message queryAll(
            HttpServletRequest request,
            @RequestParam(value = "name",required = false) String name,
            @RequestParam(value = "isAvailable",required = false) Boolean isAvailable,
            @RequestParam(value = "theme",required = false) String theme,
            @RequestParam(value = "layer",required = false) String layer
    )throws DwException {
        final DwStatisticalPeriodQueryCommand command = new DwStatisticalPeriodQueryCommand();
        command.setName(name);
        command.setEnabled(isAvailable);
        command.setTheme(theme);
        command.setLayer(layer);
        Message message = this.dwStatisticalPeriodService.queryAll(request, command);
        return message;
    }

    // query paged statistical periods

    @RequestMapping( value = "/statistical_periods", method = RequestMethod.GET)
    public Message queryPagedDecorations(
            HttpServletRequest request,
            @RequestParam(value = "page",required = false) Integer page,
            @RequestParam(value = "size",required = false) Integer size,
            @RequestParam(value = "name",required = false) String name,
            @RequestParam(value = "enabled",required = false) String enabled
    )throws DwException {
        final DwStatisticalPeriodQueryCommand command = new DwStatisticalPeriodQueryCommand();
        command.setName(name);
        command.setPage(page);
        command.setSize(size);
        if (!Strings.isNullOrEmpty(enabled)) {
            command.setEnabled(Boolean.parseBoolean(enabled));
        }
        Message message = this.dwStatisticalPeriodService.queryPage(request, command);
        return message;
    }

    // create dw statistical_periods

    @RequestMapping( value = "/statistical_periods", method = RequestMethod.POST)
    public Message create(HttpServletRequest request, @RequestBody DwStatisticalPeriodCreateCommand command) throws DwException {
        Message message = this.dwStatisticalPeriodService.create(request, command);
        return message;
    }

    // fetch one statistical_periods details by id

    @RequestMapping( value = "/statistical_periods/{id}", method = RequestMethod.GET)
    public Message getById(
            HttpServletRequest request,
            @PathVariable("id") Long id
    ) throws DwException {
        Message message = this.dwStatisticalPeriodService.getById(request, id);
        return message;
    }

    // remove statistical_periods logic

    @RequestMapping( value = "/statistical_periods/{id}", method = RequestMethod.DELETE)
    public Message deleteById(
            HttpServletRequest request,
            @PathVariable("id") Long id
    ) throws DwException {
        Message message = this.dwStatisticalPeriodService.deleteById(request, id);
        return message;
    }

    // update statistical_periods

    @RequestMapping( value = "/statistical_periods/{id}", method = RequestMethod.PUT)
    public Message update(
            HttpServletRequest request,
            @PathVariable("id") Long id,
            @RequestBody DwStatisticalPeriodUpdateCommand command
    ) throws DwException {
        command.setId(id);
        Message message = this.dwStatisticalPeriodService.update(request, command);
        return message;
    }


    // enable statistical_periods

    @RequestMapping( value = "/statistical_periods/{id}/enable", method = RequestMethod.PUT)
    public Message enable(
            HttpServletRequest request,
            @PathVariable("id") Long id
    ) throws DwException {
        Message message = this.dwStatisticalPeriodService.enable(request, id);
        return message;
    }

    // disable statistical_periods
    @RequestMapping( value = "/statistical_periods/{id}/disable", method = RequestMethod.PUT)
    public Message disable(
            HttpServletRequest request,
            @PathVariable("id") Long id
    ) throws DwException {
        Message message = this.dwStatisticalPeriodService.disable(request, id);
        return message;
    }
}
