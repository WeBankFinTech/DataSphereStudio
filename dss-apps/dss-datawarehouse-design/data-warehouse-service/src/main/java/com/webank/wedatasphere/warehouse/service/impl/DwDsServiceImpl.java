package com.webank.wedatasphere.warehouse.service.impl;

import com.webank.wedatasphere.linkis.common.exception.ErrorException;
import com.webank.wedatasphere.linkis.datasource.client.impl.LinkisMetadataSourceRemoteClient;
import com.webank.wedatasphere.linkis.datasource.client.request.GetMetadataSourceAllDatabasesAction;
import com.webank.wedatasphere.linkis.datasource.client.response.GetMetadataSourceAllDatabasesResult;
import com.webank.wedatasphere.linkis.server.Message;
import com.webank.wedatasphere.linkis.server.security.SecurityFilter;
import com.webank.wedatasphere.warehouse.LinkisRemoteClientHolder;
import com.webank.wedatasphere.warehouse.exception.DwException;
import com.webank.wedatasphere.warehouse.exception.DwExceptionCode;
import com.webank.wedatasphere.warehouse.service.DwDsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service
public class DwDsServiceImpl implements DwDsService {
    private final static Logger LOGGER = LoggerFactory.getLogger(DwDsServiceImpl.class);

    @Override
    public Message getAllHiveDbs(HttpServletRequest request) throws Exception {
        String userName = SecurityFilter.getLoginUsername(request);
        LOGGER.info("listAllDataSources userName:" + userName);
        try {
            LinkisMetadataSourceRemoteClient client = LinkisRemoteClientHolder.getMetadataSourceRemoteClient();

            GetMetadataSourceAllDatabasesAction action = GetMetadataSourceAllDatabasesAction.builder().setUser(userName).build();
            GetMetadataSourceAllDatabasesResult result = client.getAllDBMetaDataSource(action);
            List<String> dbs = result.getDbs();
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
}
