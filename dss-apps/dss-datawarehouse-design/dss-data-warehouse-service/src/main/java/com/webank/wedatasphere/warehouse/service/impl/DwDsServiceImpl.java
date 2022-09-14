package com.webank.wedatasphere.warehouse.service.impl;

import com.webank.wedatasphere.dss.data.governance.impl.LinkisDataAssetsRemoteClient;
import com.webank.wedatasphere.dss.data.governance.request.SearchHiveDbAction;
import com.webank.wedatasphere.dss.data.governance.response.SearchHiveDbResult;
import com.webank.wedatasphere.dss.framework.workspace.client.WorkSpaceRemoteClient;
import com.webank.wedatasphere.dss.framework.workspace.client.request.GetWorkspaceRolesAction;
import com.webank.wedatasphere.dss.framework.workspace.client.request.GetWorkspaceUsersAction;
import com.webank.wedatasphere.dss.framework.workspace.client.response.GetWorkspaceRolesResult;
import com.webank.wedatasphere.dss.framework.workspace.client.response.GetWorkspaceUsersResult;
import org.apache.linkis.common.exception.ErrorException;
import org.apache.linkis.server.Message;
import org.apache.linkis.server.security.SecurityFilter;
import com.webank.wedatasphere.warehouse.LinkisRemoteClientHolder;
import com.webank.wedatasphere.warehouse.exception.DwException;
import com.webank.wedatasphere.warehouse.exception.DwExceptionCode;
import com.webank.wedatasphere.warehouse.service.DwDsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class DwDsServiceImpl implements DwDsService {
    private final static Logger LOGGER = LoggerFactory.getLogger(DwDsServiceImpl.class);
    @Override
    public Message getAllHiveDbs(HttpServletRequest request,Integer limit, Integer offset) throws Exception {
        String userName = SecurityFilter.getLoginUsername(request);
        LOGGER.info("listAllDataSources userName:" + userName);
        LinkisDataAssetsRemoteClient dataAssetsRemoteClient = LinkisRemoteClientHolder.getDataAssetsRemoteClient();
        SearchHiveDbResult result = dataAssetsRemoteClient.searchHiveDb(
                SearchHiveDbAction.builder().setQuery("hive_db").setUser(userName).setOffset(offset).setLimit(limit).build());
        List<Map<String,Object>> results=result.getResult();
        List<Object> dbs=new ArrayList<>();
        for (Map<String,Object> map:results){
            dbs.add(map.get("name"));
        }
        return Message.ok().data("list", dbs);
    }

    @Override
    public Message getPrincipalUsers(HttpServletRequest request, String workspaceId) throws Exception {
        String userName = SecurityFilter.getLoginUsername(request);
        LOGGER.info("get Principal users, userName:" + userName);
        try {
            WorkSpaceRemoteClient client = LinkisRemoteClientHolder.getWorkspaceRemoteClient();
            GetWorkspaceUsersAction action = GetWorkspaceUsersAction.builder().setUser(userName).setWorkspaceId(workspaceId).setPageNow(1).setPageSize(500).build();
            GetWorkspaceUsersResult result = client.getWorkspaceUsers(action);
            List<Map<String, Object>> users = result.getWorkspaceUsers();
            return Message.ok().data("list", users);
        } catch (Exception e) {
            if (e instanceof ErrorException) {
                ErrorException ee = (ErrorException) e;
                throw new DwException(DwExceptionCode.GET_PRINCIPAL_USERS_ERROR.getCode(), e.getMessage(), ee.getIp(), ee.getPort(), ee.getServiceKind());
            } else {
                throw new DwException(DwExceptionCode.GET_PRINCIPAL_USERS_ERROR.getCode(), e.getMessage());
            }
        }
    }

    @Override
    public Message getPrincipalRoles(HttpServletRequest request, String workspaceId) throws Exception {
        String userName = SecurityFilter.getLoginUsername(request);
        LOGGER.info("get Principal roles, userName:" + userName);
        try {
            WorkSpaceRemoteClient client = LinkisRemoteClientHolder.getWorkspaceRemoteClient();
            GetWorkspaceRolesAction action = GetWorkspaceRolesAction.builder().setUser(userName).setWorkspaceId(workspaceId).build();
            GetWorkspaceRolesResult result = client.getWorkspaceRoles(action);
            List<Map<String, Object>> roles = result.getWorkspaceRoles();
            return Message.ok().data("list", roles);
        } catch (Exception e) {
            if (e instanceof ErrorException) {
                ErrorException ee = (ErrorException) e;
                throw new DwException(DwExceptionCode.GET_PRINCIPAL_ROLES_ERROR.getCode(), e.getMessage(), ee.getIp(), ee.getPort(), ee.getServiceKind());
            } else {
                throw new DwException(DwExceptionCode.GET_PRINCIPAL_ROLES_ERROR.getCode(), e.getMessage());
            }
        }
    }
}
