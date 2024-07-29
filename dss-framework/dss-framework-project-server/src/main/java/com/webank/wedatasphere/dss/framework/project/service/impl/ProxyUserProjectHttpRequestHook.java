package com.webank.wedatasphere.dss.framework.project.service.impl;

import com.webank.wedatasphere.dss.common.constant.project.ProjectUserPrivEnum;
import com.webank.wedatasphere.dss.framework.project.entity.DSSProjectUser;
import com.webank.wedatasphere.dss.framework.project.entity.request.ProjectCreateRequest;
import com.webank.wedatasphere.dss.framework.project.entity.request.ProjectDeleteRequest;
import com.webank.wedatasphere.dss.framework.project.entity.request.ProjectModifyRequest;
import com.webank.wedatasphere.dss.framework.project.entity.request.ProjectQueryRequest;
import com.webank.wedatasphere.dss.framework.project.entity.response.ProjectResponse;
import com.webank.wedatasphere.dss.framework.project.service.DSSProjectService;
import com.webank.wedatasphere.dss.framework.project.service.DSSProjectUserService;
import com.webank.wedatasphere.dss.framework.project.service.ProjectHttpRequestHook;
import com.webank.wedatasphere.dss.framework.proxy.conf.ProxyUserConfiguration;
import com.webank.wedatasphere.dss.framework.proxy.exception.DSSProxyUserErrorException;
import com.webank.wedatasphere.dss.framework.proxy.service.DssProxyUserService;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.standard.sso.utils.SSOHelper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.linkis.server.Message;
import org.apache.linkis.server.security.SecurityFilter;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;

/**
 * @author enjoyyin
 * @date 2022-09-21
 * @since 0.5.0
 */
@Component
public class ProxyUserProjectHttpRequestHook implements ProjectHttpRequestHook {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProxyUserProjectHttpRequestHook.class);
    @Autowired
    private DSSProjectService dssProjectService;
    @Autowired
    private DssProxyUserService dssProxyUserService;

    @Autowired
    private DSSProjectUserService dssProjectUserService;

    private Message doProxyUserFunction(HttpServletRequest request, Function<String, Message> function) {
        if (!ProxyUserConfiguration.isProxyUserEnable()) {
            return null;
        }
        String proxyUser;
        try {
            proxyUser = dssProxyUserService.getProxyUser(request);
        } catch (DSSProxyUserErrorException e) {
            LOGGER.error("getProxyUser Failed,cookie is :{}", Arrays.stream(request.getCookies())
                    .map(cookie -> String.format("%s=%s", cookie.getName(), cookie.getValue())).collect(Collectors.joining(",")));
            LOGGER.error("getProxyUser failed.", e);
            return Message.error(e.getMessage());
        }
        return function.apply(proxyUser);
    }

    private Message doProxyUserConsumer(HttpServletRequest request, Consumer<String> consumer) {
        return doProxyUserFunction(request, proxyUser -> {
            consumer.accept(proxyUser);
            return null;
        });
    }

    @Override
    public Message beforeGetAllProjects(HttpServletRequest request, ProjectQueryRequest projectRequest) {
        return doProxyUserConsumer(request, projectRequest::setUsername);
    }

    @Override
    public Message beforeCreateProject(HttpServletRequest request, ProjectCreateRequest projectCreateRequest) {
        return doProxyUserFunction(request, proxyUser -> {
            if (CollectionUtils.isNotEmpty(projectCreateRequest.getEditUsers()) || CollectionUtils.isNotEmpty(projectCreateRequest.getReleaseUsers())) {
                return Message.error("This environment is not allowed to set editUsers or ReleaseUsers(本环境不允许设置发布权限、编辑权限，请删除相关权限后再重试).");
            }
            String userName = SecurityFilter.getLoginUsername(request);
            if (userName.equals(proxyUser)
                    && !StringUtils.startsWithIgnoreCase(proxyUser, "WTSS_")
                    && !StringUtils.startsWithIgnoreCase(proxyUser, "hduser")) {
                return Message.error("only ops proxy user can create project(只允许代理用户创建工程).");
            }
            projectCreateRequest.getEditUsers().add(proxyUser);
            projectCreateRequest.getReleaseUsers().add(proxyUser);
            return null;
        });
    }

    @Override
    public Message beforeModifyProject(HttpServletRequest request, ProjectModifyRequest projectModifyRequest) {
        Workspace workspace = SSOHelper.getWorkspace(request);
        return doProxyUserFunction(request, proxyUser -> {
            ProjectQueryRequest projectQueryRequest = new ProjectQueryRequest();
            projectQueryRequest.setId(projectModifyRequest.getId());
            // 这里直接使用代理用户来查询
            projectQueryRequest.setUsername(proxyUser);
            projectQueryRequest.setWorkspaceId(workspace.getWorkspaceId());

            List<DSSProjectUser> dssProjectUsers = dssProjectUserService.getProjectPriv(projectModifyRequest.getId());

            if (CollectionUtils.isEmpty(dssProjectUsers)) {
                return Message.error("You have no permission to modify this project.");
            }

            Integer editPriv = ProjectUserPrivEnum.PRIV_EDIT.getRank();
            List<String> editUsers = projectModifyRequest.getEditUsers();

            Integer releasePriv = ProjectUserPrivEnum.PRIV_RELEASE.getRank();
            List<String> releaseUsers = projectModifyRequest.getReleaseUsers();

            if (!isSameUser(editPriv, dssProjectUsers, editUsers, projectModifyRequest)
                    || !isSameUser(releasePriv, dssProjectUsers, releaseUsers, projectModifyRequest)) {

                return Message.error("This environment is not allowed to set accessUsers, " +
                        "editUsers or ReleaseUsers(本环境不允许设置编辑、发布权限，请删除相关权限后再重试).");
            }

            return null;
        });
    }

    @Override
    public Message beforeDeleteProject(HttpServletRequest request, ProjectDeleteRequest projectDeleteRequest) {
        return null;
    }

    @Override
    public Message beforeGetDeletedProject(HttpServletRequest request, ProjectQueryRequest projectRequest) {
        return doProxyUserConsumer(request, projectRequest::setUsername);
    }


    public Boolean isSameUser(Integer priv, List<DSSProjectUser> dssProjectUsers, List<String> inputUser, ProjectModifyRequest projectModifyRequest) {

        // 根据priv过滤出需要的用户信息
        List<String> projectUsers = dssProjectUsers.stream()
                .filter(projectUser -> Objects.equals(priv, projectUser.getPriv()))
                .map(DSSProjectUser::getUsername).collect(Collectors.toList());

        // 过滤代理用户信息，取实名编辑用户
        List<String> realUsers = projectUsers.stream().filter(
                user -> !StringUtils.startsWithIgnoreCase(user, "WTSS_")
                        && !StringUtils.startsWithIgnoreCase(user, "hduser")
        ).collect(Collectors.toList());

        if (!CollectionUtils.isEqualCollection(inputUser, projectUsers)
                && !CollectionUtils.isEqualCollection(inputUser, realUsers)
        ) {
            return false;

        }

        // 判断输入的用户是否和实名用户一致
        if (CollectionUtils.isEqualCollection(inputUser, realUsers)) {

            if (Objects.equals(ProjectUserPrivEnum.PRIV_EDIT.getRank(), priv)) {
                projectModifyRequest.setEditUsers(projectUsers);
            } else if (Objects.equals(ProjectUserPrivEnum.PRIV_RELEASE.getRank(), priv)) {
                projectModifyRequest.setReleaseUsers(projectUsers);
            }
        }

        return true;

    }
}
