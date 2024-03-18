package com.webank.wedatasphere.warehouse.restful;

import org.apache.linkis.server.Message;
import com.webank.wedatasphere.warehouse.service.DwDsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;



@RestController
@RequestMapping(value = "/data-warehouse", produces = {"application/json;charset=utf-8"})
public class DwDsRestfulApi {

    private final DwDsService dwDsService;

    @Autowired
    public DwDsRestfulApi(DwDsService dwDsService) {
        this.dwDsService = dwDsService;
    }

    // list all hive dbs

    @RequestMapping( value = "/dbs/hive", method = RequestMethod.GET)
    public Message getAllHiveDbs(HttpServletRequest request) throws Exception {
        Message message = this.dwDsService.getAllHiveDbs(request);
        return message;
    }


    @RequestMapping( value = "/workspace/{id}/principal_users", method = RequestMethod.GET)
    public Message getAllAvailableUsers(HttpServletRequest request, @PathVariable(value = "id") String id) throws Exception {
        Message message = this.dwDsService.getPrincipalUsers(request, id);
        return message;
    }


    @RequestMapping( value = "/workspace/{id}/principal_roles", method = RequestMethod.GET)
    public Message getAllAvailableRoles(HttpServletRequest request, @PathVariable(value = "id") String id) throws Exception {
        Message message = this.dwDsService.getPrincipalRoles(request, id);
        return message;
    }

}
