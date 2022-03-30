package com.webank.wedatasphere.warehouse.restful;

import com.google.common.base.Strings;
import org.apache.linkis.server.Message;
import com.webank.wedatasphere.warehouse.cqe.*;
import com.webank.wedatasphere.warehouse.exception.DwException;
import com.webank.wedatasphere.warehouse.service.DwModifierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;


@RestController
@RequestMapping(value = "/data-warehouse", produces = {"application/json;charset=utf-8"})
public class DwModifierRestfulApi {

    private final DwModifierService dwModifierService;

    @Autowired
    public DwModifierRestfulApi(DwModifierService dwModifierService) {
        this.dwModifierService = dwModifierService;
    }


    @RequestMapping( value = "/modifiers/all", method = RequestMethod.GET)
    public Message queryAllModifiers(
            HttpServletRequest request,
            @RequestParam(value = "typeName",required = false) String typeName,
            @RequestParam(value = "isAvailable",required = false) Boolean isAvailable,
            @RequestParam(value = "theme",required = false) String theme,
            @RequestParam(value = "layer",required = false) String layer
    )throws DwException {
        final DwModifierQueryCommand command = new DwModifierQueryCommand();
        command.setName(typeName);
        command.setEnabled(isAvailable);
        command.setTheme(theme);
        command.setLayer(layer);
        Message message = this.dwModifierService.queryAllModifiers(request, command);
        return message;
    }

    // query paged modifiers
    @RequestMapping( value = "/modifiers", method = RequestMethod.GET)
    public Message queryPagedDecorations(
            HttpServletRequest request,
            @RequestParam(value = "page",required = false) Integer page,
            @RequestParam(value = "size",required = false) Integer size,
            @RequestParam(value = "name",required = false) String typeName,
            @RequestParam(value = "enabled",required = false) String enabled
    )throws DwException {
        final DwModifierQueryCommand command = new DwModifierQueryCommand();
        command.setName(typeName);
        command.setPage(page);
        command.setSize(size);
        if (!Strings.isNullOrEmpty(enabled)) {
            command.setEnabled(Boolean.parseBoolean(enabled));
        }
        Message message = this.dwModifierService.queryPage(request, command);
        return message;
    }

    // create dw decoration word
    @RequestMapping( value = "/modifiers", method = RequestMethod.POST)
    public Message create(HttpServletRequest request,@RequestBody DwModifierCreateCommand command) throws DwException {
        Message message = this.dwModifierService.create(request, command);
        return message;
    }

    // fetch one decoration details by id
    @RequestMapping( value = "/modifiers/{id}", method = RequestMethod.GET)
    public Message getById(
            HttpServletRequest request,
            @PathVariable("id") Long id
    ) throws DwException {
        Message message = this.dwModifierService.getById(request, id);
        return message;
    }

    // remove decoration logic
    @RequestMapping( value = "/modifiers/{id}", method = RequestMethod.DELETE)
    public Message deleteById(
            HttpServletRequest request,
            @PathVariable("id") Long id
    ) throws DwException {
        Message message = this.dwModifierService.deleteById(request, id);
        return message;
    }

    // update
    @RequestMapping( value = "/modifiers/{id}", method = RequestMethod.PUT)
    public Message update(
            HttpServletRequest request,
            @PathVariable("id") Long id,
            @RequestBody DwModifierUpdateCommand command
    ) throws DwException {
        command.setId(id);
        Message message = this.dwModifierService.update(request, command);
        return message;
    }

    // enable modifier

    @RequestMapping( value = "/modifiers/{id}/enable", method = RequestMethod.PUT)
    public Message enable(
            HttpServletRequest request,
            @PathVariable("id") Long id
    ) throws DwException {
        Message message = this.dwModifierService.enable(request, id);
        return message;
    }

    // disable modifier
    @RequestMapping( value = "/modifiers/{id}/disable", method = RequestMethod.PUT)
    public Message disable(
             HttpServletRequest request,
            @PathVariable("id") Long id
    ) throws DwException {
        Message message = this.dwModifierService.disable(request, id);
        return message;
    }
}
