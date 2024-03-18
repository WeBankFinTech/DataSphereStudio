package com.webank.wedatasphere.warehouse.restful;

import org.apache.linkis.server.Message;
import com.webank.wedatasphere.warehouse.cqe.*;
import com.webank.wedatasphere.warehouse.exception.DwException;
import com.webank.wedatasphere.warehouse.service.DwThemeDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = "/data-warehouse", produces = {"application/json;charset=utf-8"})
public class DwThemeDomainRestfulApi {

    private final DwThemeDomainService dwThemeDomainService;

    @Autowired
    public DwThemeDomainRestfulApi(DwThemeDomainService dwThemeDomainService) {
        this.dwThemeDomainService = dwThemeDomainService;
    }

    // query paged theme domains

    @RequestMapping( value = "/themedomains", method = RequestMethod.GET)
    public Message queryPagedCustomLayers(
            HttpServletRequest request,
            @RequestParam(value = "page",required = false) Integer page,
            @RequestParam(value = "size",required = false) Integer size,
            @RequestParam(value = "name",required = false) String name,
            @RequestParam(value = "enabled",required = false) Boolean enabled
    )throws DwException {
        final DwThemeDomainQueryCommand command = new DwThemeDomainQueryCommand();
        command.setName(name);
        command.setPage(page);
        command.setSize(size);
        command.setEnabled(enabled);
        Message message = this.dwThemeDomainService.queryPage(request, command);
        return message;
    }


    @RequestMapping( value = "/themedomains/all", method = RequestMethod.GET)
    public Message queryAllThemeDomains(
            HttpServletRequest request,
            @RequestParam(value = "name",required = false) String name,
            @RequestParam(value = "isAvailable",required = false) Boolean isAvailable
    )throws DwException {
        final DwThemeDomainQueryCommand command = new DwThemeDomainQueryCommand();
        command.setName(name);
        command.setEnabled(isAvailable);
        Message message = this.dwThemeDomainService.queryAllThemeDomains(request, command);
        return message;
    }

    // create theme domain
    @RequestMapping( value = "/themedomains", method = RequestMethod.POST)
    public Message createDwCustomLayer(HttpServletRequest request,@RequestBody DwThemeDomainCreateCommand command) throws DwException {
        Message message = this.dwThemeDomainService.create(request, command);
        return message;
    }

    // get theme domain by id
    @RequestMapping( value = "/themedomains/{id}", method = RequestMethod.GET)
    public Message getLayerById(
            HttpServletRequest request,
            @PathVariable("id") Long id
    ) throws DwException {
        Message message = this.dwThemeDomainService.getById(request, id);
        return message;
    }

    // delete theme domain

    @RequestMapping( value = "/themedomains/{id}", method = RequestMethod.DELETE)
    public Message deleteById(
            HttpServletRequest request,
            @PathVariable("id") Long id
    ) throws DwException {
        Message message = this.dwThemeDomainService.deleteById(request, id);
        return message;
    }

    // update theme domain

    @RequestMapping( value = "/themedomains/{id}", method = RequestMethod.PUT)
    public Message update(
            HttpServletRequest request,
            @PathVariable("id") Long id,
            @RequestBody DwThemeDomainUpdateCommand command
    ) throws DwException {
        command.setId(id);
        Message message = this.dwThemeDomainService.update(request, command);
        return message;
    }

    // enable theme domain

    @RequestMapping( value = "/themedomains/{id}/enable", method = RequestMethod.PUT)
    public Message enable(
            HttpServletRequest request,
            @PathVariable("id") Long id
    ) throws DwException {
        Message message = this.dwThemeDomainService.enable(request, id);
        return message;
    }

    // disable theme domain
    @RequestMapping( value = "/themedomains/{id}/disable", method = RequestMethod.PUT)
    public Message disable(
            HttpServletRequest request,
            @PathVariable("id") Long id
    ) throws DwException {
        Message message = this.dwThemeDomainService.disable(request, id);
        return message;
    }
}
