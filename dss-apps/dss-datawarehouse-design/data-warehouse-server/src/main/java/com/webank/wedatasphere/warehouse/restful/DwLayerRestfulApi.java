package com.webank.wedatasphere.warehouse.restful;

import org.apache.linkis.server.Message;
import com.webank.wedatasphere.warehouse.cqe.DwLayerCreateCommand;
import com.webank.wedatasphere.warehouse.cqe.DwLayerQueryCommand;
import com.webank.wedatasphere.warehouse.cqe.DwLayerUpdateCommand;
import com.webank.wedatasphere.warehouse.exception.DwException;
import com.webank.wedatasphere.warehouse.service.DwLayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;



@RestController
@RequestMapping(value = "/data-warehouse", produces = {"application/json;charset=utf-8"})
public class DwLayerRestfulApi {

    private final DwLayerService dwLayerService;

    @Autowired
    public DwLayerRestfulApi(DwLayerService dwLayerService) {
        this.dwLayerService = dwLayerService;
    }

    // list all preset layers

    @RequestMapping( value = "/layers/preset", method = RequestMethod.GET)
    public Message getAllPresetLayers(HttpServletRequest request) throws DwException {
        Message message = this.dwLayerService.getAllPresetLayers(request);
        return message;
    }


    @RequestMapping( value = "/layers/all", method = RequestMethod.GET)
    public Message getAllLayers(HttpServletRequest request, @RequestParam(value = "isAvailable",required = false) Boolean isAvailable, @RequestParam(value = "db",required = false) String db) throws DwException {
        Message message = this.dwLayerService.getAllLayers(request, isAvailable, db);
        return message;
    }

    // query paged custom layers

    @RequestMapping( value = "/layers/custom", method = RequestMethod.GET)
    public Message queryPagedCustomLayers(
            HttpServletRequest request,
            @RequestParam(value = "page",required = false) Integer page,
            @RequestParam(value = "size",required = false) Integer size,
            @RequestParam(value = "name",required = false) String name,
            @RequestParam(value = "enabled",required = false) Boolean enabled
    )throws DwException {
        final DwLayerQueryCommand command = new DwLayerQueryCommand();
        command.setName(name);
        command.setEnabled(enabled);
        command.setPage(page);
        command.setSize(size);
        Message message = this.dwLayerService.queryPagedCustomLayers(request, command);
        return message;
    }

    // create custom layer
    @RequestMapping( value = "/layers/custom", method = RequestMethod.POST)
    public Message createDwCustomLayer( HttpServletRequest request,@RequestBody DwLayerCreateCommand command) throws DwException {
        Message message = this.dwLayerService.createDwCustomLayer(request, command);
        return message;
    }

    // get layer by id
    @RequestMapping( value = "/layers/{id}", method = RequestMethod.GET)
    public Message getLayerById(
            HttpServletRequest request,
            @PathVariable("id") Long id
    ) throws DwException {
        Message message = this.dwLayerService.getLayerById(request, id);
        return message;
    }

    // delete layer

    @RequestMapping( value = "/layers/{id}", method = RequestMethod.DELETE)
    public Message deleteById(
            HttpServletRequest request,
            @PathVariable("id") Long id
    ) throws DwException {
        Message message = this.dwLayerService.deleteById(request, id);
        return message;
    }

    // update layer
    @RequestMapping( value = "/layers/{id}", method = RequestMethod.PUT)
    public Message update(
            HttpServletRequest request,
            @PathVariable("id") Long id,
            @RequestBody DwLayerUpdateCommand command
    ) throws DwException {
        command.setId(id);
        Message message = this.dwLayerService.update(request, command);
        return message;
    }

    // enable layer
    @RequestMapping( value = "/layers/{id}/enable", method = RequestMethod.PUT)
    public Message enable(
            HttpServletRequest request,
            @PathVariable("id") Long id
    ) throws DwException {
        Message message = this.dwLayerService.enable(request, id);
        return message;
    }

    // disable layer

    @RequestMapping( value = "/layers/{id}/disable", method = RequestMethod.PUT)
    public Message disable(
            HttpServletRequest request,
            @PathVariable("id") Long id
    ) throws DwException {
        Message message = this.dwLayerService.disable(request, id);
        return message;
    }
}
