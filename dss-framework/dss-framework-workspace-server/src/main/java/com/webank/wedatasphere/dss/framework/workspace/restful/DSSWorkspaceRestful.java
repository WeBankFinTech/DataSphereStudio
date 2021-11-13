/*
 * Copyright 2019 WeBank
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.webank.wedatasphere.dss.framework.workspace.restful;
import com.webank.wedatasphere.dss.framework.workspace.bean.DSSWorkspace;
import com.webank.wedatasphere.dss.framework.workspace.bean.request.CreateWorkspaceRequest;
import com.webank.wedatasphere.dss.framework.workspace.bean.vo.DSSWorkspaceHomePageVO;
import com.webank.wedatasphere.dss.framework.workspace.bean.vo.DSSWorkspaceOverviewVO;
import com.webank.wedatasphere.dss.framework.workspace.bean.vo.DSSWorkspaceVO;
import com.webank.wedatasphere.dss.framework.workspace.bean.vo.DepartmentVO;
import com.webank.wedatasphere.dss.framework.workspace.service.DSSWorkspaceMenuService;
import com.webank.wedatasphere.dss.framework.workspace.service.DSSWorkspaceService;
import com.webank.wedatasphere.dss.framework.workspace.util.WorkspaceDBHelper;
import org.apache.linkis.common.exception.ErrorException;
import org.apache.linkis.server.Message;
import org.apache.linkis.server.security.SecurityFilter;
import org.codehaus.jackson.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import static com.webank.wedatasphere.dss.framework.workspace.util.DSSWorkspaceConstant.WORKSPACE_ID_STR;

@RequestMapping(path = "/dss/framework/workspace", produces = {"application/json"})
@RestController
public class DSSWorkspaceRestful {
    @Autowired
    private DSSWorkspaceService dssWorkspaceService;
    @Autowired
    private DSSWorkspaceMenuService dssWorkspaceMenuService;
    @Autowired
    private WorkspaceDBHelper workspaceDBHelper;

    @RequestMapping(path ="createWorkspace", method = RequestMethod.POST)
    public Message createWorkspace(@Context HttpServletRequest request, @RequestBody CreateWorkspaceRequest createWorkspaceRequest)throws ErrorException {
        String userName = SecurityFilter.getLoginUsername(request);
        if (!dssWorkspaceService.checkAdmin(userName)){
            return Message.error("您好，您不是管理员,没有权限建立工作空间");
        }
        String workSpaceName = createWorkspaceRequest.getWorkspaceName();
        String department = createWorkspaceRequest.getDepartment();
        String description = createWorkspaceRequest.getDescription();
        String stringTags = createWorkspaceRequest.getTags();
        String productName = createWorkspaceRequest.getProductName();
        int workspaceId = dssWorkspaceService.createWorkspace(workSpaceName, stringTags, userName, description, department, productName);
        return Message.ok().data("workspaceId", workspaceId).data("workspaceName",workSpaceName);
    }

    @RequestMapping(path ="listDepartments", method = RequestMethod.GET)
    public Message listDepartments(@Context HttpServletRequest request, @RequestParam(WORKSPACE_ID_STR) String workspaceId){
        //todo 要从um中获取
        List<DepartmentVO> departments  = dssWorkspaceService.getDepartments();
        return Message.ok().data("departments", departments);
    }

    @RequestMapping(path ="getWorkspaces", method = RequestMethod.GET)
    public Message getWorkspaces(@Context HttpServletRequest request){
        String username = SecurityFilter.getLoginUsername(request);
        List<DSSWorkspace> workspaces = dssWorkspaceService.getWorkspaces(username);
        List<DSSWorkspaceVO> dssWorkspaceVOS = new ArrayList<>();
        for (DSSWorkspace workspace:workspaces ){
            String name = workspace.getName();
            int id = workspace.getId();
            String labels = workspace.getLabel();
            DSSWorkspaceVO dssWorkspaceVO = new DSSWorkspaceVO();
            dssWorkspaceVO.setId(id);
            dssWorkspaceVO.setName(name);
            dssWorkspaceVO.setTags(labels);
            dssWorkspaceVO.setDepartment(workspace.getDepartment());
            dssWorkspaceVO.setDescription(workspace.getDescription());
            dssWorkspaceVO.setProduct(workspace.getProduct());
            dssWorkspaceVOS.add(dssWorkspaceVO);
        }
        //todo 获取用户所有所有能够访问的工作空间
        return Message.ok().data("workspaces", dssWorkspaceVOS);
    }

    @RequestMapping(path ="getWorkspaceHomePage", method = RequestMethod.GET)
    public Message getWorkspaceHomePage(@Context HttpServletRequest request, @RequestParam(required = false, name = "micro_module") String moduleName){
        //如果用户的工作空间大于两个，那么就直接返回/workspace页面
        String username = SecurityFilter.getLoginUsername(request);
        DSSWorkspaceHomePageVO dssWorkspaceHomePageVO = dssWorkspaceService.getWorkspaceHomePage(username,moduleName);
        return Message.ok().data("workspaceHomePage", dssWorkspaceHomePageVO);
    }

    @RequestMapping(path ="getOverview", method = RequestMethod.GET)
    public Message getOverview(@Context HttpServletRequest request, @RequestParam(WORKSPACE_ID_STR) int workspaceId){
        String username = SecurityFilter.getLoginUsername(request);
        String language = request.getHeader("Content-language");
        boolean isEnglish = "en".equals(language);
        DSSWorkspaceOverviewVO dssWorkspaceOverviewVO = dssWorkspaceService.getOverview(username, workspaceId, isEnglish);
        return Message.ok().data("overview", dssWorkspaceOverviewVO);
    }

    @RequestMapping(path ="refreshCache", method = RequestMethod.GET)
    public Message refreshCache(@Context HttpServletRequest request){
        workspaceDBHelper.retrieveFromDB();
        return Message.ok("refresh ok");
    }

}

