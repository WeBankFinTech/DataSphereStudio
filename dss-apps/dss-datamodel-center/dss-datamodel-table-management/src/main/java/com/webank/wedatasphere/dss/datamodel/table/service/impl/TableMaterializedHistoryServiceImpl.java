package com.webank.wedatasphere.dss.datamodel.table.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.webank.wedatasphere.dss.datamodel.center.common.constant.ErrorCode;
import com.webank.wedatasphere.dss.datamodel.center.common.exception.DSSDatamodelCenterException;
import com.webank.wedatasphere.dss.datamodel.center.common.launcher.CommonDataModelJobLauncher;
import com.webank.wedatasphere.dss.datamodel.center.common.launcher.DataExistsDataModelJobLauncher;
import com.webank.wedatasphere.dss.datamodel.center.common.launcher.DataModelJobTaskBuilder;
import com.webank.wedatasphere.dss.datamodel.center.common.launcher.DataModelJobTask;
import com.webank.wedatasphere.dss.datamodel.table.dao.DssDatamodelTableMaterializedHistoryMapper;
import com.webank.wedatasphere.dss.datamodel.table.entity.DssDatamodelTable;
import com.webank.wedatasphere.dss.datamodel.table.entity.DssDatamodelTableColumns;
import com.webank.wedatasphere.dss.datamodel.table.entity.DssDatamodelTableMaterializedHistory;
import com.webank.wedatasphere.dss.datamodel.table.materialized.HiveSchemaDdlBuilder;
import com.webank.wedatasphere.dss.datamodel.table.service.TableColumnsService;
import com.webank.wedatasphere.dss.datamodel.table.service.TableMaterializedHistoryService;
import com.webank.wedatasphere.linkis.common.exception.ErrorException;
import com.webank.wedatasphere.linkis.computation.client.interactive.SubmittableInteractiveJob;
import org.apache.commons.lang.StringUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class TableMaterializedHistoryServiceImpl extends ServiceImpl<DssDatamodelTableMaterializedHistoryMapper, DssDatamodelTableMaterializedHistory> implements TableMaterializedHistoryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TableMaterializedHistoryServiceImpl.class);

    @Resource
    private ModelMapper modelMapper;

    @Resource
    private TableColumnsService tableColumnsService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer materializedTable(DssDatamodelTable current) throws ErrorException {

        //判断是否有数据
        checkData(current);
        //不存在数据删除表
        dropTable(current);
        String sql = buildSql(current);
        LOGGER.info("table id : {}, sql : {}", current.getId(), sql);
        //创建表
        SubmittableInteractiveJob job = createTable(current, sql);

        if (!job.isSucceed()) {
            LOGGER.error("errorCode : {},  table  id : {} create table error job : {}, sql : {}", ErrorCode.TABLE_CREATE_ERROR.getCode(), current.getId(), job.getId(), sql);
            //throw new DSSDatamodelCenterException(ErrorCode.TABLE_CREATE_ERROR.getCode(), " table id : " + current.getId() + " create table error job : " + job.getId() + ", sql : " + sql);
        }

        DssDatamodelTableMaterializedHistory newOne = modelMapper.map(current, DssDatamodelTableMaterializedHistory.class);
        newOne.setId(null);
        newOne.setMaterializedCode(sql);
        newOne.setCreateTime(new Date());
        newOne.setStatus(job.isSucceed() ? 0 : 1);//
        newOne.setLastUpdateTime(new Date());
        newOne.setReason("reason");
        newOne.setTaskId(job.getId());
        getBaseMapper().insert(newOne);
        return 1;
    }

    public void checkData(DssDatamodelTable current) throws ErrorException {
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

    private SubmittableInteractiveJob createTable(DssDatamodelTable current, String sql) {
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

    private void dropTable(DssDatamodelTable current) {
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
        if (current.getIsExternal() == 1 && StringUtils.isNotBlank(current.getLocation())) {
            builder.withExternal(current.getLocation());
        }
        columns.forEach(column -> {
            builder.addColumn(column.getName(), column.getType(), column.getIsPartitionField() != 0, column.getComment());
        });
        builder.storedType(current.getFileType());
        return builder.createTableString();
    }

    @Override
    public String generateSql(DssDatamodelTable current) {
        return buildSql(current);
    }
}
