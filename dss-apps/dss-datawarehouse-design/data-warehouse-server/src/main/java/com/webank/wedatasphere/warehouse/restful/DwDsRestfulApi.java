package com.webank.wedatasphere.warehouse.restful;

import org.apache.linkis.server.Message;
import com.webank.wedatasphere.warehouse.service.DwDsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;

@RequestMapping(path = "/data-warehouse", produces = {"application/json"})
@RestController
public class DwDsRestfulApi {
    private static final String DEFAULT_LIMIT = "25";
    private static final String DEFAULT_OFFSET = "0";
    private final DwDsService dwDsService;



    @Autowired
    public DwDsRestfulApi(DwDsService dwDsService) {
        this.dwDsService = dwDsService;
    }

    // list all hive dbs
//    @RequestMapping(value = "/dbs/hive",method = RequestMethod.GET)
//    public Message getAllHiveDbs(HttpServletRequest request) throws Exception {
//        Message message = this.dwDsService.getAllHiveDbs(request);
//        return message;
//    }
    @RequestMapping(value = "/dbs/hive",method = RequestMethod.GET)
    public Message getAllHiveDbs(HttpServletRequest request, @RequestParam(value="limit",defaultValue = DEFAULT_LIMIT) int limit,@RequestParam(value="offset",defaultValue = DEFAULT_OFFSET)  int offset) throws Exception {
        Message message = this.dwDsService.getAllHiveDbs(request,limit,offset);
        return message;
    }


    @RequestMapping(value = "/workspace/{id}/principal_users",method = RequestMethod.GET)
    public Message getAllAvailableUsers(HttpServletRequest request, @PathVariable(value = "id") String id) throws Exception {
        Message message = this.dwDsService.getPrincipalUsers(request, id);
        return message;
    }

    @RequestMapping(value = "/workspace/{id}/principal_roles",method = RequestMethod.GET)
    public Message getAllAvailableRoles(HttpServletRequest request, @PathVariable(value = "id") String id) throws Exception {
        Message message = this.dwDsService.getPrincipalRoles(request, id);
        return message;
    }
}
