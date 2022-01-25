package com.webank.wedatasphere.dss.framework.admin.restful;

import com.webank.wedatasphere.dss.framework.admin.pojo.entity.DssExchangeTaskRes;
import com.webank.wedatasphere.dss.framework.admin.pojo.entity.DssExchangisProject;
import com.webank.wedatasphere.dss.framework.admin.service.DssExchangeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.linkis.server.Message;
import org.apache.linkis.server.security.SecurityFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * @Auther: Han Tang
 * @Date: 2022/1/11-01-11-14:20
 */


@RestController
@RequestMapping(path = "/dss/framework/exchangis", produces = {"application/json"})
@Slf4j
public class DssExchangisController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DssExchangisController.class);
    @Autowired
    DssExchangeService dssExchangeService;


    @RequestMapping(path = "/project/tree", method = RequestMethod.GET)
    public Message getProjectTree(HttpServletRequest request) throws Exception {
        String userName = SecurityFilter.getLoginUsername(request);
//        String userName = "admin";
        List<DssExchangisProject> dssExchangisProjects = dssExchangeService.queryExchangeProject(userName);
        Message message = Message.ok().data("response", dssExchangisProjects);
        return message;
    }


    @RequestMapping(path = "/task/tree", method = RequestMethod.GET)
    public Message getTaskTree(HttpServletRequest request, @RequestParam int projectId, @RequestParam int pageNum,@RequestParam(required = false) String fuzzyName) throws Exception {
        String userName = SecurityFilter.getLoginUsername(request);
//        String userName = "admin";
        DssExchangeTaskRes dssExchangeTaskRes = dssExchangeService.queryExchangeTask(projectId, userName,pageNum,fuzzyName);
        Message message = Message.ok().data("response",dssExchangeTaskRes);
        return message;
    }

    @RequestMapping(path = "/shell", method = RequestMethod.GET)
    public Message getShellScript(HttpServletRequest request, @RequestParam int taskId,@RequestParam int projectId) {
        String userName = "admin";
        HashMap<String,String> map = new HashMap<>();
        String shellScript = dssExchangeService.getSellScript(taskId,projectId);
        map.put("shell",shellScript);
        Message message = Message.ok().data("response",map);
        return message;
    }


}


