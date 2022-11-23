package com.webank.wedatasphere.dss.framework.workspace.restful;

import com.webank.wedatasphere.dss.framework.workspace.bean.request.ECInstanceRequest;
import com.webank.wedatasphere.dss.framework.workspace.bean.request.KillECInstanceRequest;
import com.webank.wedatasphere.dss.framework.workspace.bean.vo.EngineConnItemVO;
import com.webank.wedatasphere.dss.framework.workspace.service.DSSWorkspacePrivService;
import com.webank.wedatasphere.dss.framework.workspace.service.DSSWorkspaceService;
import com.webank.wedatasphere.dss.framework.workspace.util.WorkspaceDBHelper;
import org.apache.linkis.server.Message;
import org.apache.linkis.server.security.SecurityFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * 工作空间内资源管理先关接口
 * Author: xlinliu
 * Date: 2022/11/22
 */
@RequestMapping(path = "/dss/framework/workspace", produces = {"application/json"})
@RestController
public class DSSWorkspaceResourceManageRestful {
    private static final Logger LOGGER = LoggerFactory.getLogger(DSSWorkspaceResourceManageRestful.class);
    @Autowired
    HttpServletRequest httpServletRequest;

    @Autowired
    DSSWorkspaceService dssWorkspaceService;
    @Autowired
    DSSWorkspacePrivService dssWorkspacePrivService;
    @Autowired
    WorkspaceDBHelper workspaceDBHelper;
    @PostMapping("listEngineConnInstances")
    public Message listEngineConnInstances(@RequestBody ECInstanceRequest request){
        String userName=  SecurityFilter.getLoginUsername(httpServletRequest);
        if (!dssWorkspaceService.checkAdmin(userName)) {
            return Message.error("you have no permission to admin workspace!(您好，您不是管理员，没有权限管理空间)");
        }


        return Message.ok("成功获取引擎列表！").data("engineList",new ArrayList<EngineConnItemVO>()).data("total",0);
    }
    @PostMapping("killEngineConnInstances")
    public Message listEngineConnInstances(@RequestBody List<KillECInstanceRequest> killECInstanceRequests){



        return Message.ok("引擎停止请求发送成功，可稍后通过搜索查询引擎状态！");
    }
}
