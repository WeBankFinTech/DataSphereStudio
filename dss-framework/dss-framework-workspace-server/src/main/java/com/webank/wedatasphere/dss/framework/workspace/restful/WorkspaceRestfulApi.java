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
import com.webank.wedatasphere.dss.framework.workspace.bean.dto.response.HomepageDemoMenuVo;
import com.webank.wedatasphere.dss.framework.workspace.bean.dto.response.HomepageVideoVo;
import com.webank.wedatasphere.dss.framework.workspace.bean.dto.response.OnestopMenuVo;
import com.webank.wedatasphere.dss.framework.workspace.bean.dto.response.WorkspaceFavoriteVo;
import com.webank.wedatasphere.dss.framework.workspace.bean.request.AddFavoriteRequest;
import com.webank.wedatasphere.dss.framework.workspace.bean.request.AddWorkspaceRequest;
import com.webank.wedatasphere.dss.framework.workspace.bean.vo.DepartmentVO;
import com.webank.wedatasphere.dss.framework.workspace.service.DSSWorkspaceService;
import org.apache.linkis.common.exception.ErrorException;
import org.apache.linkis.server.Message;
import org.apache.linkis.server.security.SecurityFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping(path = "/dss/framework/workspace", produces = {"application/json"})
public class WorkspaceRestfulApi {
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkspaceRestfulApi.class);
    @Autowired
    private DSSWorkspaceService dssWorkspaceService;

    @RequestMapping(path = "workspaces", method = RequestMethod.GET)
    public Message getAllWorkspaces(@Context HttpServletRequest req) {
        // TODO: Order By time
        String username = SecurityFilter.getLoginUsername(req);
        List<DSSWorkspace> workspaces = dssWorkspaceService.getWorkspaces(username);
        return Message.ok().data("workspaces", workspaces);
    }

    @RequestMapping(path = "/workspaces/{id}", method = RequestMethod.GET)
    public Message getWorkspacesById(@Context HttpServletRequest req, @PathVariable("id") Long id) {
        DSSWorkspace workspace = dssWorkspaceService.getWorkspacesById(id);
        return Message.ok().data("workspace", workspace);
    }

    @RequestMapping(path = "/workspaces/departments", method = RequestMethod.GET)
    public Message getAllWorkspaceDepartments(@Context HttpServletRequest req) {
        List<DepartmentVO> departments = dssWorkspaceService.getDepartments();
        return Message.ok().data("departments", departments);
    }

    @RequestMapping(path ="/workspaces/exists", method = RequestMethod.GET)
    public Message getUsernameExistence(@Context HttpServletRequest req, @RequestParam(required = false, name = "name") String name) {
        boolean exists = dssWorkspaceService.existWorkspaceName(name);
        return Message.ok().data("workspaceNameExists", exists);
    }

    @RequestMapping(path = "/workspaces", method = RequestMethod.POST)
    public Message addWorkspace(@Context HttpServletRequest req,
                                @RequestBody AddWorkspaceRequest addWorkspaceRequest) throws ErrorException {
        String name = addWorkspaceRequest.getName();
        String department = addWorkspaceRequest.getDepartment();
        String label = addWorkspaceRequest.getLabel();
        String description = addWorkspaceRequest.getDescription();
        String userName = SecurityFilter.getLoginUsername(req);
        if (!dssWorkspaceService.checkAdmin(userName)) {
            return Message.error("您好，您不是管理员,没有权限建立工作空间");
        }
        if (dssWorkspaceService.existWorkspaceName(name)) {
            return Message.error("工作空间名重复");
        }
        String productName = "DSS";
        int workspaceId = dssWorkspaceService.createWorkspace(name, label, userName, description, department, productName);
        return Message.ok().data("workspaceId", workspaceId);
    }

    @RequestMapping(path ="/workspaces/demos", method = RequestMethod.GET)
    public Message getAllHomepageDemos(@Context HttpServletRequest req) {
        String header = req.getHeader("Content-language").trim();
        boolean isChinese = "zh-CN".equals(header);
        List<HomepageDemoMenuVo> homepageDemos = dssWorkspaceService.getHomepageDemos(isChinese);
        return Message.ok().data("demos", homepageDemos);
    }

    @RequestMapping(path ="/workspaces/videos", method = RequestMethod.GET)
    public Message getAllVideos(@Context HttpServletRequest req) {
        String header = req.getHeader("Content-language").trim();
        boolean isChinese = "zh-CN".equals(header);
        List<HomepageVideoVo> homepageVideos = dssWorkspaceService.getHomepageVideos(isChinese);
        return Message.ok().data("videos", homepageVideos);
    }

    @RequestMapping(path ="/workspaces/{workspaceId}/managements", method = RequestMethod.GET)
    public Message getWorkspaceManagements(@Context HttpServletRequest req, @PathVariable("workspaceId") Long workspaceId) {
        String header = req.getHeader("Content-language").trim();
        boolean isChinese = "zh-CN".equals(header);
        String username = SecurityFilter.getLoginUsername(req);
        List<OnestopMenuVo> managements = dssWorkspaceService.getWorkspaceManagements(workspaceId, username, isChinese);
        return Message.ok().data("managements", managements);
    }

    @RequestMapping(path ="workspaces/{workspaceId}/applications", method = RequestMethod.GET)
    public Message getWorkspaceApplications(@Context HttpServletRequest req, @PathVariable("workspaceId") Long workspaceId) {
        String header = req.getHeader("Content-language").trim();
        boolean isChinese = "zh-CN".equals(header);
        String username = SecurityFilter.getLoginUsername(req);
        List<OnestopMenuVo> applications = dssWorkspaceService.getWorkspaceApplications(workspaceId, username, isChinese);
        return Message.ok().data("applications", applications);
    }

    @RequestMapping(path ="/workspaces/{workspaceId}/favorites", method = RequestMethod.GET)
    public Message getWorkspaceFavorites(@Context HttpServletRequest req, @PathVariable("workspaceId") Long workspaceId) {
        String header = req.getHeader("Content-language").trim();
        boolean isChinese = "zh-CN".equals(header);
        String username = SecurityFilter.getLoginUsername(req);
        List<WorkspaceFavoriteVo> favorites = dssWorkspaceService.getWorkspaceFavorites(workspaceId, username, isChinese);
        Set<WorkspaceFavoriteVo> favoriteVos = new HashSet<>(favorites);
        return Message.ok().data("favorites", favoriteVos);
    }

    /**
     * 应用加入收藏，返回收藏后id
     *
     * @param req
     * @return
     */
    @RequestMapping(path ="/workspaces/{workspaceId}/favorites", method = RequestMethod.POST)
    public Message addFavorite(@Context HttpServletRequest req, @PathVariable("workspaceId") Long workspaceId,
                               @RequestBody AddFavoriteRequest addFavoriteRequest) {
        String username = SecurityFilter.getLoginUsername(req);
        Long favoriteId = dssWorkspaceService.addFavorite(username, workspaceId, addFavoriteRequest.getMenuApplicationId());
        return Message.ok().data("favoriteId", favoriteId);
    }

    @RequestMapping(path ="/workspaces/{workspaceId}/favorites/{favouritesId}", method = RequestMethod.DELETE)
    public Message deleteFavorite(@Context HttpServletRequest req, @PathVariable("workspaceId") Long workspaceId, @PathVariable("favouritesId") Long favouritesId) {
        LOGGER.info("deleteFavorite--workspaceId={}--favouritesId={}",workspaceId,favouritesId);
        String username = SecurityFilter.getLoginUsername(req);
        Long favoriteId = dssWorkspaceService.deleteFavorite(username, favouritesId);
        return Message.ok().data("favoriteId", favoriteId);
    }

}
