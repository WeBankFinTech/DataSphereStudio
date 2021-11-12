package com.webank.wedatasphere.warehouse.service.impl;

import com.webank.wedatasphere.linkis.datasource.client.impl.LinkisDataSourceRemoteClient;
import com.webank.wedatasphere.linkis.datasource.client.request.QueryDataSourceAction;
import com.webank.wedatasphere.linkis.datasource.client.response.QueryDataSourceResult;
import com.webank.wedatasphere.linkis.datasourcemanager.common.domain.DataSource;
import com.webank.wedatasphere.linkis.server.Message;
import com.webank.wedatasphere.linkis.server.security.SecurityFilter;
import com.webank.wedatasphere.warehouse.LinkisRemoteClientHolder;
import com.webank.wedatasphere.warehouse.client.DataSourceDTO;
import com.webank.wedatasphere.warehouse.service.DwDsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class DwDsServiceImpl implements DwDsService {
    private final static Logger LOGGER = LoggerFactory.getLogger(DwDsServiceImpl.class);

    @Override
    public Message getAllHiveDbs(HttpServletRequest request) throws Exception {
        String userName = SecurityFilter.getLoginUsername(request);
//        String userName = "hdfs";
        LOGGER.info("listAllDataSources userName:" + userName);

        LinkisDataSourceRemoteClient linkisDataSourceRemoteClient = LinkisRemoteClientHolder.getLinkisDataSourceRemoteClient();
        QueryDataSourceAction.Builder builder = QueryDataSourceAction.builder()
                .setSystem("system")
                .setIdentifies("")
                .setName("hive")
                .setCurrentPage(1)
                .setPageSize(500)
                .setUser(userName)
                ;

        List<DataSource> allDataSource;
        try {
            QueryDataSourceResult result = linkisDataSourceRemoteClient.queryDataSource(builder.build());
            allDataSource = result.getAllDataSource();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e.getCause());
            throw e;
        }
        List<DataSourceDTO> dataSources = new ArrayList<>();
        if (!Objects.isNull(allDataSource)) {
            allDataSource.forEach(ds -> {
                DataSourceDTO item = new DataSourceDTO();
                item.setId(ds.getId());
                item.setCreateIdentify(ds.getCreateIdentify());
                item.setName(ds.getDataSourceName());
                item.setType(ds.getDataSourceType().getName());
                item.setDataSourceTypeId(ds.getDataSourceTypeId());
                dataSources.add(item);
            });
        }
        return Message.ok().data("list", dataSources);
    }
}
