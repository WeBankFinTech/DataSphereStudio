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

package com.webank.wedatasphere.dss.standard.sso.utils;

import com.webank.wedatasphere.dss.common.conf.DSSCommonConf;
import com.webank.wedatasphere.dss.common.utils.DomainUtils;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.standard.app.sso.builder.SSOBuilderService;
import com.webank.wedatasphere.dss.standard.app.sso.builder.SSOUrlBuilderOperation;
import com.webank.wedatasphere.dss.standard.common.exception.AppStandardWarnException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SSOHelper {

    private static final String WORKSPACE_ID_COOKIE_KEY = "workspaceId";
    private static final String WORKSPACE_NAME_COOKIE_KEY = "workspaceName";
    private static SSOBuilderService ssoBuilderService;

    private static final Logger LOGGER = LoggerFactory.getLogger(SSOHelper.class);

    public static void setSSOBuilderService(SSOBuilderService ssoBuilderService) {
        if(ssoBuilderService == null) {
            return;
        }
        SSOHelper.ssoBuilderService = ssoBuilderService;
    }

    public static Workspace getWorkspace(Map<String, String> cookies) {
        Workspace workspace = new Workspace();
        workspace.setCookies(cookies);
        if(cookies.isEmpty() || !cookies.containsKey(WORKSPACE_NAME_COOKIE_KEY)) {
            throw new AppStandardWarnException(50010, "Cannot find workspace info from cookies map, please ensure cookies have transferred correctly.");
        }
        String workspaceName = cookies.get(WORKSPACE_NAME_COOKIE_KEY);
        workspace.setWorkspaceName(workspaceName);
        if(cookies.containsKey(WORKSPACE_ID_COOKIE_KEY)) {
            long workspaceId = Long.parseLong(cookies.get(WORKSPACE_ID_COOKIE_KEY));
            workspace.setWorkspaceId(workspaceId);
        }
        return workspace;
    }

    public static void addWorkspaceInfo(HttpServletRequest request, Workspace workspace) {
        String gatewayUrl = request.getHeader("GATEWAY_URL");
        if(StringUtils.isNotBlank(gatewayUrl)) {
            if(gatewayUrl.startsWith("http")) {
                workspace.setDssUrl(gatewayUrl);
            } else {
                workspace.setDssUrl("http://" + gatewayUrl);
            }
        }
        Cookie[] cookies = request.getCookies();
        Arrays.stream(cookies).forEach(cookie -> workspace.addCookie(cookie.getName(), cookie.getValue()));
        if(StringUtils.isBlank(workspace.getWorkspaceName())) {
            throw new AppStandardWarnException(50010, "Cannot find workspace info from cookies, please ensure front-web has injected cookie['workspaceName'](不能找到工作空间名，请确认前端是否已经注入cookie['workspaceName']).");
        }
        if(workspace.getWorkspaceId() <= 0) {
            throw new AppStandardWarnException(50010, "Cannot find workspace info from cookies, please ensure front-web has injected cookie['workspaceId'](不能找到工作空间名，请确认前端是否已经注入cookie['workspaceId']).");
        }
    }

    public static Workspace getWorkspace(HttpServletRequest request){
        Workspace workspace = new Workspace();
        Cookie[] cookies = request.getCookies();
        Arrays.stream(cookies).forEach(cookie -> {
            if(WORKSPACE_NAME_COOKIE_KEY.equals(cookie.getName())) {
                workspace.setWorkspaceName(cookie.getValue());
            } else if(WORKSPACE_ID_COOKIE_KEY.equals(cookie.getName())) {
                workspace.setWorkspaceId(Long.parseLong(cookie.getValue()));
            }
        });
        addWorkspaceInfo(request, workspace);
        return workspace;
    }

    public static Workspace setAndGetWorkspace(HttpServletRequest request, HttpServletResponse response, long workspaceId, String workspaceName) {
        boolean isWorkspaceExists = Arrays.stream(request.getCookies())
                .anyMatch(cookie -> WORKSPACE_ID_COOKIE_KEY.equals(cookie.getName()) && workspaceId == Long.parseLong(cookie.getValue())) &&
                Arrays.stream(request.getCookies())
                        .anyMatch(cookie -> WORKSPACE_NAME_COOKIE_KEY.equals(cookie.getName()) && workspaceName.equals(cookie.getValue()));
        if (isWorkspaceExists) {
            LOGGER.warn("workspace {} already exists in DSS cookies, ignore to set it again.", workspaceName);
            return getWorkspace(request);
        }
        String workspaceIdStr = String.valueOf(workspaceId);
        String domain = getCookieDomain(DomainUtils.getCookieDomain(request));
        Cookie workspaceIdCookie = new Cookie(WORKSPACE_ID_COOKIE_KEY, workspaceIdStr);
        workspaceIdCookie.setPath("/");
//        workspaceIdCookie.setDomain(domain);
        Cookie workspaceNameCookie = new Cookie(WORKSPACE_NAME_COOKIE_KEY, workspaceName);
        workspaceNameCookie.setPath("/");
//        workspaceNameCookie.setDomain(domain);
        response.addCookie(workspaceIdCookie);
        response.addCookie(workspaceNameCookie);
        Workspace workspace = new Workspace();
        workspace.setWorkspaceId(workspaceId);
        workspace.setWorkspaceName(workspaceName);
        addWorkspaceInfo(request, workspace);
        LOGGER.info("Try to change the workspace to [{}] in DSS cookies of domain({}), the workspace info is {}.",
                workspaceName, domain, workspace);
        workspace.setWorkspaceId(workspaceId);
        workspace.setWorkspaceName(workspaceName);
        workspace.addCookie(WORKSPACE_ID_COOKIE_KEY, workspaceIdStr);
        workspace.addCookie(WORKSPACE_NAME_COOKIE_KEY, workspaceName);
        return workspace;
    }

    public static SSOUrlBuilderOperation createSSOUrlBuilderOperation(Workspace workspace) {
        SSOUrlBuilderOperation operation = ssoBuilderService.createSSOUrlBuilderOperation();
        setSSOUrlBuilderOperation(operation, workspace);
        return operation;
    }

    public static void setSSOUrlBuilderOperation(SSOUrlBuilderOperation operation, Workspace workspace) {
        workspace.getCookies().forEach(operation::addCookie);
        operation.setDSSUrl(workspace.getDssUrl());
        operation.setWorkspace(workspace.getWorkspaceName());
    }

    private static final Pattern DOMAIN_REGEX = Pattern.compile("[a-zA-Z][a-zA-Z0-9\\.]+");
    private static final Pattern IP_REGEX = Pattern.compile("([^:]+):.+");

    /**
     * "dss.com" -> ".dss.com"
     * "127.0.0.1" -> "127.0.0.1"
     * "127.0.0.1:8080" -> "127.0.0.1"
     * @param host the Host in HttpRequest Headers
     * @return
     */
    public static String getCookieDomain(String host) {
        int level = DSSCommonConf.DSS_DOMAIN_LEVEL.getValue();
        if(host.startsWith("https://")) {
            host = host.substring(8);
        } else if(host.startsWith("http://")) {
            host = host.substring(7);
        }
        if (host.endsWith("/")) {
            host = host.substring(0, host.length() - 1);
        }
        if(DOMAIN_REGEX.matcher(host).find()) {
            String[] domains = host.split("\\.");
            int index = level;
            if (domains.length == level) {
                index = level - 1;
            } else if (domains.length < level) {
                index = domains.length;
            }
            if (index < 0) {
                return host;
            }
            String[] parsedDomains = Arrays.copyOfRange(domains, index, domains.length);
            if (parsedDomains.length < level) {
                return host;
            }
            String domain = String.join(".", parsedDomains);
            if(domains.length >= level) {
                return "." + domain;
            }
            return domain;
        }
        Matcher matcher = IP_REGEX.matcher(host);
        if(matcher.find()) {
            return matcher.group(1);
        } else {
            return host;
        }
    }

}
