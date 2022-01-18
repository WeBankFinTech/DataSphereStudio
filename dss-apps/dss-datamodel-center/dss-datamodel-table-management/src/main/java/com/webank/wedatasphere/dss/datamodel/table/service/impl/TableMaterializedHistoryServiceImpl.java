package com.webank.wedatasphere.dss.datamodel.table.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.webank.wedatasphere.dss.data.governance.entity.QueryType;
import com.webank.wedatasphere.dss.data.governance.impl.LinkisDataAssetsRemoteClient;
import com.webank.wedatasphere.dss.data.governance.request.HiveTblSizeAction;
import com.webank.wedatasphere.dss.data.governance.request.SearchHiveTblAction;
import com.webank.wedatasphere.dss.data.governance.response.SearchHiveTblResult;
import com.webank.wedatasphere.dss.datamodel.center.common.constant.ColumnType;
import com.webank.wedatasphere.dss.datamodel.center.common.constant.ErrorCode;
import com.webank.wedatasphere.dss.datamodel.center.common.constant.TabelExternalType;
import com.webank.wedatasphere.dss.datamodel.center.common.context.DataModelSecurityContextHolder;
import com.webank.wedatasphere.dss.datamodel.center.common.dto.CreateTableDTO;
import com.webank.wedatasphere.dss.datamodel.center.common.exception.DSSDatamodelCenterException;
import com.webank.wedatasphere.dss.datamodel.center.common.launcher.*;
import com.webank.wedatasphere.dss.datamodel.center.common.ujes.launcher.CreateTableDataModelUJESJobLauncher;
import com.webank.wedatasphere.dss.datamodel.center.common.ujes.launcher.DataExistsDataModelUJESJobLauncher;
import com.webank.wedatasphere.dss.datamodel.center.common.ujes.launcher.DropTableDataModelUJESJobLauncher;
import com.webank.wedatasphere.dss.datamodel.center.common.ujes.task.CreateTableDataModelUJESJobTask;
import com.webank.wedatasphere.dss.datamodel.center.common.ujes.task.DataExistsDataModelUJESJobTask;
import com.webank.wedatasphere.dss.datamodel.center.common.ujes.task.DataModelUJESJobTask;
import com.webank.wedatasphere.dss.datamodel.center.common.ujes.task.DropTableDataModelUJESJobTask;
import com.webank.wedatasphere.dss.datamodel.table.dao.DssDatamodelTableMaterializedHistoryMapper;
import com.webank.wedatasphere.dss.datamodel.table.dto.HiveTblSimpleInfoDTO;
import com.webank.wedatasphere.dss.datamodel.table.entity.DssDatamodelTable;
import com.webank.wedatasphere.dss.datamodel.table.entity.DssDatamodelTableColumns;
import com.webank.wedatasphere.dss.datamodel.table.entity.DssDatamodelTableMaterializedHistory;
import com.webank.wedatasphere.dss.datamodel.table.materialized.HiveSchemaDdlBuilder;
import com.webank.wedatasphere.dss.datamodel.table.service.TableColumnsService;
import com.webank.wedatasphere.dss.datamodel.table.service.TableMaterializedHistoryService;
import org.apache.linkis.common.exception.ErrorException;
import org.apache.linkis.computation.client.interactive.SubmittableInteractiveJob;
import org.apache.commons.lang.StringUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class TableMaterializedHistoryServiceImpl extends ServiceImpl<DssDatamodelTableMaterializedHistoryMapper, DssDatamodelTableMaterializedHistory> implements TableMaterializedHistoryService{

    private static final Logger LOGGER = LoggerFactory.getLogger(TableMaterializedHistoryServiceImpl.class);

    @Resource
    private ModelMapper modelMapper;

    @Resource
    private TableColumnsService tableColumnsService;

    @Resource
    private LinkisDataAssetsRemoteClient linkisDataAssetsRemoteClient;

    @Resource
    private DataExistsDataModelUJESJobLauncher dataExistsDataModelUJESJobLauncher;

    @Resource
    private DropTableDataModelUJESJobLauncher dropTableDataModelUJESJobLauncher;

    @Resource
    private CreateTableDataModelUJESJobLauncher createTableDataModelUJESJobLauncher;

    private final Gson assertsGson = new GsonBuilder().setDateFormat("yyyy MM-dd HH:mm:ss").create();

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer materializedTable(DssDatamodelTable current, String user) throws ErrorException {

        //判断是否有数据
        checkData(current, user);
        //不存在数据删除表
        dropTable(current.getName(),user);

        CreateTableDTO dto = createTable(current, user);

        //创建表
//        SubmittableInteractiveJob job = linkisCreateTable(current, sql);
//
//        if (!job.isSucceed()) {
//            LOGGER.error("errorCode : {},  table  id : {} create table error job : {}, sql : {}", ErrorCode.TABLE_CREATE_ERROR.getCode(), current.getId(), job.getId(), sql);
//            //throw new DSSDatamodelCenterException(ErrorCode.TABLE_CREATE_ERROR.getCode(), " table id : " + current.getId() + " create table error job : " + job.getId() + ", sql : " + sql);
//        }


        DssDatamodelTableMaterializedHistory newOne = modelMapper.map(current, DssDatamodelTableMaterializedHistory.class);
        newOne.setId(null);
        newOne.setMaterializedCode(dto.getSql());
        newOne.setCreateTime(new Date());
        newOne.setStatus(dto.getStatus() == 0 ? 0 : 1);//
        newOne.setLastUpdateTime(new Date());
        newOne.setReason("reason");
        newOne.setTaskId(dto.getTaskId());
        newOne.setTablename(current.getName());
        getBaseMapper().insert(newOne);
        return 1;
    }

    public void checkData(DssDatamodelTable current, String user) throws ErrorException {
        //linkisJobCheck(current);
        //如果表存在且存在数据
        if (hasData(current.getName(), user)) {
            LOGGER.error("errorCode : {}, table id : {} has data", ErrorCode.TABLE_CHECK_ERROR.getCode(), current.getId());
            throw new DSSDatamodelCenterException(ErrorCode.TABLE_CHECK_ERROR.getCode(), " table id : " + current.getId() + " has data");
        }
    }

    @Override
    public boolean hasData(String tableName, String user) {
//        DataModelUJESJobTask dataModelUJESJobTask = DataExistsDataModelUJESJobTask.newBuilder().user(user).code(tableName).build();
//        return dataExistsDataModelUJESJobLauncher.launch(dataModelUJESJobTask);
        String tblName = StringUtils.substringAfter(tableName,".");
        String dbName = StringUtils.substringBefore(tableName,".");
        return linkisDataAssetsRemoteClient.
                searchHiveTblSize(HiveTblSizeAction.builder().setUser(user).setDbName(dbName).setTableName(tblName).build())
                .getResult() >0 ;
    }

    @Override
    public boolean tableExists(String tableName, String user) throws ErrorException {
        SearchHiveTblResult result = linkisDataAssetsRemoteClient.searchHiveTbl(SearchHiveTblAction.builder().setUser(user).setQuery(tableName).setPrecise(QueryType.PRECISE).setOffset(0).setLimit(30).build());
        List<HiveTblSimpleInfoDTO> dtos = assertsGson.fromJson(assertsGson.toJson(result.getResult()), new TypeToken<List<HiveTblSimpleInfoDTO>>() {
        }.getType());
        if (CollectionUtils.isEmpty(dtos)){
            return false;
        }
        for(HiveTblSimpleInfoDTO dto : dtos){
            if (StringUtils.equals(StringUtils.substringBeforeLast(dto.getQualifiedName(),"@"),tableName)){
                return true;
            }
        }
        return false;
    }


    @Override
    public boolean dropTable(String tableName, String user) throws ErrorException {
//        if(!tableExists(tableName)){
//            return true;
//        }
        DataModelUJESJobTask dataModelUJESJobTask = DropTableDataModelUJESJobTask.newBuilder().user(user).code(tableName).build();
        return dropTableDataModelUJESJobLauncher.launch(dataModelUJESJobTask);
    }

    @Override
    public CreateTableDTO createTable(DssDatamodelTable current, String user) throws ErrorException {
        String sql = buildSql(current);
        LOGGER.info("table id : {}, sql : {}", current.getId(), sql);
        DataModelUJESJobTask dataModelUJESJobTask = CreateTableDataModelUJESJobTask.newBuilder().user(user).code(sql).build();
        CreateTableDTO createTableDTO = createTableDataModelUJESJobLauncher.launch(dataModelUJESJobTask);
        createTableDTO.setSql(sql);
        return createTableDTO;
    }

    private void linkisJobCheck(DssDatamodelTable current) throws DSSDatamodelCenterException {
        //校验是否已数据
        DataModelJobTaskBuilder dataModELJobTaskBuilder = new DataModelJobTaskBuilder();
        DataModelJobTask task = dataModELJobTaskBuilder.withDataExistsExchangisJobTask()
                .code(current.getName())
                .creator("hdfs")
                .executeUser("hdfs")
                .engineType("hive-2.3.3")
                .runType("hql")
                .build();
        DataExistsDataModelJobLauncher launcher = new DataExistsDataModelJobLauncher();
        Integer result = launcher.launch(task);
        if (result > 0) {
            LOGGER.error("errorCode : {}, table id : {} has data", ErrorCode.TABLE_CREATE_ERROR.getCode(), current.getId());
            throw new DSSDatamodelCenterException(ErrorCode.TABLE_CREATE_ERROR.getCode(), " table id : " + current.getId() + " has data");
        }
    }

    private SubmittableInteractiveJob linkisCreateTable(DssDatamodelTable current, String sql) {
        DataModelJobTaskBuilder dataModELJobTaskBuilder = new DataModelJobTaskBuilder();
        DataModelJobTask commonTask = dataModELJobTaskBuilder.withCommonExchangisJobTask()
                .code(sql)
                .creator("hdfs")
                .executeUser("hdfs")
                .engineType("hive-2.3.3")
                .runType("hql")
                .build();
        CommonDataModelJobLauncher commonExchangisJobLauncher = new CommonDataModelJobLauncher();
        return commonExchangisJobLauncher.launch(commonTask);
    }

    private void linkisDropTable(DssDatamodelTable current) {
        DataModelJobTaskBuilder dataModELJobTaskBuilder = new DataModelJobTaskBuilder();
        DataModelJobTask commonTask = dataModELJobTaskBuilder.withCommonExchangisJobTask()
                .code(String.format("drop table if exists %s", current.getName()))
                .creator("hdfs")
                .executeUser("hdfs")
                .engineType("hive-2.3.3")
                .runType("hql")
                .build();
        CommonDataModelJobLauncher commonExchangisJobLauncher = new CommonDataModelJobLauncher();
        commonExchangisJobLauncher.launch(commonTask);
    }

    private String buildSql(DssDatamodelTable current) {
        List<DssDatamodelTableColumns> columns = tableColumnsService.list(Wrappers.<DssDatamodelTableColumns>lambdaQuery().eq(DssDatamodelTableColumns::getTableId, current.getId()));
        HiveSchemaDdlBuilder builder = new HiveSchemaDdlBuilder();
        builder.tableName(current.getName());
        if (StringUtils.isNotBlank(current.getComment())) {
            builder.comment(current.getComment());
        }
        if (Objects.equals(current.getIsExternal(), TabelExternalType.EXTERNAL.getCode())) {
            builder.withExternal();
        }
        if (StringUtils.isNotBlank(current.getLocation())) {
            builder.location(current.getLocation());
        }
        columns.forEach(column -> {
            builder.addColumn(column.getName(), column.getType(), ColumnType.PARTITION_KEY.getCode().equals(column.getIsPartitionField()), column.getComment());
        });
        builder.storedType(current.getFileType());
        return builder.createTableString();
    }

    @Override
    public String generateSql(DssDatamodelTable current) {
        return buildSql(current);
    }


    @Override
    public boolean isMaterialized(String tableName, String version) throws ErrorException {
        return tableExists(tableName, DataModelSecurityContextHolder.getContext().getDataModelAuthentication().getUser());

//        return getBaseMapper().selectCount(Wrappers.<DssDatamodelTableMaterializedHistory>lambdaQuery()
//                .eq(DssDatamodelTableMaterializedHistory::getTablename, tableName)
//                .eq(DssDatamodelTableMaterializedHistory::getVersion, version)
//                .eq(DssDatamodelTableMaterializedHistory::getStatus, 0)) > 0 && tableExists(tableName, DataModelSecurityContextHolder.getContext().getDataModelAuthentication().getUser());
    }

    @Override
    public Optional<HiveTblSimpleInfoDTO> getHiveTblSimpleInfoByName(String tableName, String user) throws ErrorException {
        
        SearchHiveTblResult result = linkisDataAssetsRemoteClient.searchHiveTbl(SearchHiveTblAction.builder().setPrecise(QueryType.PRECISE).setUser(user).setQuery(tableName).setOffset(0).setLimit(1).build());
        List<HiveTblSimpleInfoDTO> dtos = assertsGson.fromJson(assertsGson.toJson(result.getResult()), new TypeToken<List<HiveTblSimpleInfoDTO>>() {
        }.getType());
        if (CollectionUtils.isEmpty(dtos)){
            return Optional.empty();
        }
        for(HiveTblSimpleInfoDTO dto : dtos){
            if (StringUtils.equals(StringUtils.substringBeforeLast(dto.getQualifiedName(),"@"),tableName)){
                return Optional.of(dto);
            }
        }
        return Optional.empty();
    }
}
