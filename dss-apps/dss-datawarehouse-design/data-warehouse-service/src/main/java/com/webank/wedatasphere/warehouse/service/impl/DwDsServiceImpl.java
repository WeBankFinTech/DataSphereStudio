package com.webank.wedatasphere.warehouse.service.impl;

import com.webank.wedatasphere.dss.framework.workspace.client.WorkSpaceRemoteClient;
import com.webank.wedatasphere.dss.framework.workspace.client.request.GetWorkspaceRolesAction;
import com.webank.wedatasphere.dss.framework.workspace.client.request.GetWorkspaceUsersAction;
import com.webank.wedatasphere.dss.framework.workspace.client.response.GetWorkspaceRolesResult;
import com.webank.wedatasphere.dss.framework.workspace.client.response.GetWorkspaceUsersResult;
import org.apache.linkis.common.exception.ErrorException;
import org.apache.linkis.datasource.client.impl.LinkisMetadataSourceRemoteClient;
import org.apache.linkis.datasource.client.response.GetMetadataSourceAllDatabasesResult;
import org.apache.linkis.server.Message;
import org.apache.linkis.server.security.SecurityFilter;
import com.webank.wedatasphere.warehouse.LinkisRemoteClientHolder;
import com.webank.wedatasphere.warehouse.exception.DwException;
import com.webank.wedatasphere.warehouse.exception.DwExceptionCode;
import com.webank.wedatasphere.warehouse.service.DwDsService;
import org.apache.linkis.ujes.client.UJESClient;
import org.apache.linkis.ujes.client.request.GetDBSAction;
import org.apache.linkis.ujes.client.response.GetDBSResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Service
public class DwDsServiceImpl implements DwDsService {
    private final static Logger LOGGER = LoggerFactory.getLogger(DwDsServiceImpl.class);

    @Override
    public Message getAllHiveDbs(HttpServletRequest request) throws Exception {
        String userName = SecurityFilter.getLoginUsername(request);
        LOGGER.info("listAllDataSources userName:" + userName);
        try {
            UJESClient client = LinkisRemoteClientHolder.getMetadataSourceRemoteClient();

            GetDBSAction action = GetDBSAction.builder().setUser(userName).build();
            GetDBSResult result = client.getDBS(action);
            List<String> dbs = result.getDBSName();
            return Message.ok().data("list", dbs);
        } catch (Exception e) {
            if (e instanceof ErrorException) {
                ErrorException ee = (ErrorException) e;
                throw new DwException(DwExceptionCode.GET_AVAILABLE_DBS_ERROR.getCode(), e.getMessage(), ee.getIp(), ee.getPort(), ee.getServiceKind());
            } else {
                throw new DwException(DwExceptionCode.GET_AVAILABLE_DBS_ERROR.getCode(), e.getMessage());
            }
        }

//        LinkisDataSourceRemoteClient linkisDataSourceRemoteClient = LinkisRemoteClientHolder.getLinkisDataSourceRemoteClient();
//        QueryDataSourceAction.Builder builder = QueryDataSourceAction.builder()
//                .setSystem("system")
//                .setIdentifies("")
//                .setName("hive")
//                .setCurrentPage(1)
//                .setPageSize(500)
//                .setUser(userName)
//                ;
//
//        List<DataSource> allDataSource;
//        try {
//            QueryDataSourceResult result = linkisDataSourceRemoteClient.queryDataSource(builder.build());
//            allDataSource = result.getAllDataSource();
//        } catch (Exception e) {
//            LOGGER.error(e.getMessage(), e.getCause());
//            throw e;
//        }
//        List<DataSourceDTO> dataSources = new ArrayList<>();
//        if (!Objects.isNull(allDataSource)) {
//            allDataSource.forEach(ds -> {
//                DataSourceDTO item = new DataSourceDTO();
//                item.setId(ds.getId());
//                item.setCreateIdentify(ds.getCreateIdentify());
//                item.setName(ds.getDataSourceName());
//                item.setType(ds.getDataSourceType().getName());
//                item.setDataSourceTypeId(ds.getDataSourceTypeId());
//                dataSources.add(item);
//            });
//        }
//        return Message.ok().data("list", dataSources);
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
