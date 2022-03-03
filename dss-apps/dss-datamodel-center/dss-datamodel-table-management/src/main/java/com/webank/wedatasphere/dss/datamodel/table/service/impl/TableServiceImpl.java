package com.webank.wedatasphere.dss.datamodel.table.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.webank.wedatasphere.dss.data.governance.entity.ClassificationConstant;
import com.webank.wedatasphere.dss.data.governance.entity.QueryType;
import com.webank.wedatasphere.dss.data.governance.impl.LinkisDataAssetsRemoteClient;
import com.webank.wedatasphere.dss.data.governance.request.*;
import com.webank.wedatasphere.dss.data.governance.response.*;
import com.webank.wedatasphere.dss.datamodel.center.common.constant.ErrorCode;
import com.webank.wedatasphere.dss.datamodel.center.common.constant.LabelConstant;
import com.webank.wedatasphere.dss.datamodel.center.common.constant.ModeType;
import com.webank.wedatasphere.dss.datamodel.center.common.context.DataModelSecurityContextHolder;
import com.webank.wedatasphere.dss.datamodel.center.common.dto.PreviewDataDTO;
import com.webank.wedatasphere.dss.datamodel.center.common.exception.DSSDatamodelCenterException;
import com.webank.wedatasphere.dss.datamodel.center.common.ujes.task.DataModelUJESJobTask;
import com.webank.wedatasphere.dss.datamodel.center.common.ujes.launcher.PreviewDataModelUJESJobLauncher;
import com.webank.wedatasphere.dss.datamodel.center.common.ujes.task.PreviewDataModelUJESJobTask;
import com.webank.wedatasphere.dss.datamodel.table.dao.DssDatamodelTableMapper;
import com.webank.wedatasphere.dss.datamodel.table.dao.TableQueryMapper;
import com.webank.wedatasphere.dss.datamodel.table.dto.*;
import com.webank.wedatasphere.dss.datamodel.table.entity.*;
import com.webank.wedatasphere.dss.datamodel.table.event.*;
import com.webank.wedatasphere.dss.datamodel.table.service.*;
import com.webank.wedatasphere.dss.datamodel.table.vo.*;
import org.apache.linkis.common.exception.ErrorException;
import org.apache.linkis.server.Message;
import org.apache.linkis.ujes.client.exception.UJESJobException;
import org.apache.commons.lang.StringUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class TableServiceImpl extends ServiceImpl<DssDatamodelTableMapper, DssDatamodelTable> implements TableService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TableServiceImpl.class);

    @Resource
    private TableColumnsService tableColumnsService;

    @Resource
    private TableVersionService tableVersionService;

    @Resource
    private TableStatsService tableStatsService;

    @Resource
    private ModelMapper modelMapper;

    @Resource
    private TableCollectService tableCollectService;

    @Resource
    private TableDictionaryService tableDictionaryService;

    @Resource
    private TableMaterializedHistoryService tableMaterializedHistoryService;

    @Resource
    private LinkisDataAssetsRemoteClient linkisDataAssetsRemoteClient;

    @Resource
    private PreviewDataModelUJESJobLauncher previewDataModelUJESJobLauncher;

    @Resource
    private TableQueryMapper tableQueryMapper;

    private Gson gson = new Gson();

    @Resource
    private ApplicationEventPublisher publisher;

    private final Gson assertsGson = new GsonBuilder().setDateFormat("yyyy MM-dd HH:mm:ss").create();

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addTable(TableAddVO vo) throws ErrorException {

        int sameCount = getBaseMapper().selectCount(Wrappers.<DssDatamodelTable>lambdaQuery().eq(DssDatamodelTable::getName, vo.getName()));
        if (sameCount > 0) {
            LOGGER.error("errorCode : {}, table name can not repeat, name : {}", ErrorCode.TABLE_ADD_ERROR.getCode(), vo.getName());
            throw new DSSDatamodelCenterException(ErrorCode.TABLE_ADD_ERROR.getCode(), "table name can not repeat");
        }

        DssDatamodelTable newOne = modelMapper.map(vo, DssDatamodelTable.class);
        newOne.setCreateTime(new Date());
        newOne.setUpdateTime(new Date());
        newOne.setVersion("1");
        getBaseMapper().insert(newOne);

        Long tableId = newOne.getId();
        List<DssDatamodelTableColumns> columns = addColumns(tableId, vo.getColumns());

        String user = DataModelSecurityContextHolder.getContext().getDataModelAuthentication().getUser();

        //发布表绑定模型事件
        publisher.publishEvent(new BindModelByTableEvent(this, user, newOne));
        publisher.publishEvent(new BindModelByColumnsEvent(this, user, newOne.getName(), columns));

        //发布绑定标签事件
        publisher.publishEvent(new BindLabelByTableEvent(this, user, newOne));

        return newOne.getId();
    }


    private List<DssDatamodelTableColumns> addColumns(Long tableId, List<TableColumnVO> columnVOs) throws ErrorException {
        List<DssDatamodelTableColumns> columns = Lists.newArrayList();
        for (TableColumnVO columnVO : columnVOs) {
            DssDatamodelTableColumns newColumn = modelMapper.map(columnVO, DssDatamodelTableColumns.class);
            columns.add(newColumn);
        }

        tableColumnsService.batchInsert(tableId, columns);
        return columns;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateTable(Long id, TableUpdateVO vo) throws ErrorException {

        DssDatamodelTable org = getBaseMapper().selectById(id);
        if (org == null) {
            LOGGER.error("errorCode : {}, update table error not exists", ErrorCode.TABLE_UPDATE_ERROR.getCode());
            throw new DSSDatamodelCenterException(ErrorCode.TABLE_UPDATE_ERROR.getCode(), "update table error not exists");
        }

        //判断数据表是否有数据
        tableMaterializedHistoryService.checkData(org, vo.getCreator());

        String user = DataModelSecurityContextHolder.getContext().getDataModelAuthentication().getUser();

        //当更新表名称时,存在其他名称同名或者当前名称已经存在版本信息，则不允许修改指标名称
        if (!StringUtils.equals(vo.getName(), org.getName())) {
            //表如果已经物化不能修改名称
            if (tableMaterializedHistoryService.tableExists(org.getName(), user)) {
                LOGGER.error("errorCode : {}, table name can not change as table has materialized", ErrorCode.TABLE_UPDATE_ERROR.getCode());
                throw new DSSDatamodelCenterException(ErrorCode.TABLE_UPDATE_ERROR.getCode(), "table name can not change as table has materialized");
            }
            int repeat = getBaseMapper().selectCount(Wrappers.<DssDatamodelTable>lambdaQuery().eq(DssDatamodelTable::getName, vo.getName()));
            String lastVersion = tableVersionService.findLastVersion(org.getName());
            if (repeat > 0 || StringUtils.isNotBlank(lastVersion)) {
                LOGGER.error("errorCode : {}, table name can not repeat or has version", ErrorCode.TABLE_UPDATE_ERROR.getCode());
                throw new DSSDatamodelCenterException(ErrorCode.TABLE_UPDATE_ERROR.getCode(), "table name can not repeat or has version");
            }
        }

        Long orgId = org.getId();
        String version = org.getVersion();
        List<DssDatamodelTableColumns> orgColumns = tableColumnsService.listByTableId(id);

        DssDatamodelTable updateOne = modelMapper.map(vo, DssDatamodelTable.class);
        updateOne.setUpdateTime(new Date());
        int result = getBaseMapper().update(updateOne, Wrappers.<DssDatamodelTable>lambdaUpdate().eq(DssDatamodelTable::getId, id));
        if (result != 1) {
            LOGGER.error("errorCode : {}, update table error not exists", ErrorCode.TABLE_UPDATE_ERROR.getCode());
            throw new DSSDatamodelCenterException(ErrorCode.TABLE_UPDATE_ERROR.getCode(), "update table error not exists");
        }
        List<DssDatamodelTableColumns> newColumns = Lists.newArrayList();
        for (TableColumnVO columnVO : vo.getColumns()) {
            DssDatamodelTableColumns newColumn = modelMapper.map(columnVO, DssDatamodelTableColumns.class);
            newColumns.add(newColumn);
        }

        tableColumnsService.batchUpdate(orgId, newColumns);


        publisher.publishEvent(new UpdateBindModelByTableEvent(this, user, org, updateOne));
        publisher.publishEvent(new UpdateBindModelByColumnsEvent(this, user, org.getName(), orgColumns, newColumns));
        publisher.publishEvent(new UpdateBindLabelByTableEvent(this, user, org, updateOne));

        return 1;
    }


    @Override
    public TableQueryDTO queryByName(TableQueryOneVO vo) throws ErrorException {
        DssDatamodelTable table = getBaseMapper().selectOne(Wrappers.<DssDatamodelTable>lambdaQuery().eq(DssDatamodelTable::getName, vo.getName()));
        //如果查询不到则查询资产
        if (table == null) {
            //查资产 guid 086c5785-8bda-4756-8ba6-46f9c3d597f1  a3be4a97-6465-4c3d-adee-76dfa662e531  ef09c10a-e156-4d09-96af-af30eb3af26a
            GetHiveTblBasicResult result = linkisDataAssetsRemoteClient.getHiveTblBasic(GetHiveTblBasicAction.builder().setUser(vo.getUser()).setGuid(vo.getGuid()).build());
            HiveTblDetailInfoDTO dto = assertsGson.fromJson(assertsGson.toJson(result.getResult()), HiveTblDetailInfoDTO.class);
            HiveTblStatsResult hiveTblStatsResult = linkisDataAssetsRemoteClient.searchHiveTblStats(HiveTblStatsAction.builder().setUser(vo.getUser()).setGuid(vo.getGuid()).build());
            return TableQueryDTO.toTableStatsDTO(dto, hiveTblStatsResult.getInfo(), StringUtils.substringBefore(dto.getBasic().getQualifiedName(),"@"));
        }
        return queryTable(table);
    }

    private TableQueryDTO queryTable(DssDatamodelTable table) throws ErrorException {
        TableQueryDTO tableQueryDTO = modelMapper.map(table, TableQueryDTO.class);

        List<TableColumnQueryDTO> columnQueryDTOS = tableColumnsService.listByTableId(table.getId())
                .stream().map(column -> modelMapper.map(column, TableColumnQueryDTO.class)).collect(Collectors.toList());
        tableQueryDTO.setColumns(columnQueryDTOS);

//        DssDatamodelTableStats tableStats = tableStatsService.queryByTableName(table.getName());
//        if (tableStats != null) {
//            tableQueryDTO.setStats(modelMapper.map(tableStats, TableStatsDTO.class));
//        }
        TableHeadlineDTO headlineDTO = new TableHeadlineDTO();
        headlineDTO.setStorageType(0);
        headlineDTO.setTableType(0);
        headlineDTO.setEntityType(
                tableMaterializedHistoryService.isMaterialized(table.getName(), table.getVersion()) ? 1 : 0);
        tableQueryDTO.setHeadline(headlineDTO);

        String user = DataModelSecurityContextHolder.getContext().getDataModelAuthentication().getUser();
        Integer collectionCount = tableCollectService.getBaseMapper().selectCount(Wrappers.<DssDatamodelTableCollcetion>lambdaQuery().eq(DssDatamodelTableCollcetion::getName, table.getName()));
        String dbName = StringUtils.substringBefore(table.getName(), ".");
        String tblName = StringUtils.substringAfter(table.getName(), ".");
        HiveTblStatsResult hiveTblStatsResult = linkisDataAssetsRemoteClient.searchHiveTblStats(HiveTblStatsAction.builder().setUser(user)
                .setTableName(tblName)
                .setDbName(dbName)
                .build());
        tableQueryDTO.setStats(TableStatsDTO.from(hiveTblStatsResult.getInfo(), collectionCount));

        tableQueryDTO.setLastAccessTime(tableMaterializedHistoryService.getHiveTblSimpleInfoByName(table.getName(),user).map(HiveTblSimpleInfoDTO::getLastAccessTime).orElse(null));

        return tableQueryDTO;
    }


    @Override
    public TableQueryDTO queryById(Long id) throws ErrorException {
        DssDatamodelTable table = getBaseMapper().selectOne(Wrappers.<DssDatamodelTable>lambdaQuery().eq(DssDatamodelTable::getId, id));
        if (table == null) {
            LOGGER.error("errorCode : {}, table id : {} not exists", ErrorCode.TABLE_QUERY_ERROR.getCode(), id);
            throw new DSSDatamodelCenterException(ErrorCode.TABLE_QUERY_ERROR.getCode(), "table  id " + id + " not exists");
        }
        return queryTable(table);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer addTableVersion(Long id, TableVersionAddVO vo) throws ErrorException {

        //判断旧版本指标是否存在
        DssDatamodelTable orgVersion = getBaseMapper().selectById(id);
        if (orgVersion == null) {
            LOGGER.error("errorCode : {}, table id : {} not exists", ErrorCode.TABLE_VERSION_ADD_ERROR.getCode(), id);
            throw new DSSDatamodelCenterException(ErrorCode.TABLE_VERSION_ADD_ERROR.getCode(), "table  id " + id + " not exists");
        }

        //判断旧版本是否有数据
        tableMaterializedHistoryService.checkData(orgVersion, vo.getCreator());
        //没有数据删除表
        //tableMaterializedHistoryService.dropTable(orgVersion.getName(),vo.getCreator());

        String orgName = orgVersion.getName();
        String orgDatabase = orgVersion.getDataBase();
        String orgV = orgVersion.getVersion();
        String assignVersion = newVersion(orgName, orgV);

        //表名称和数据库不能改变
        if (!StringUtils.equals(orgName, vo.getName()) || !StringUtils.equals(orgDatabase, vo.getDataBase())) {
            LOGGER.error("errorCode : {}, table name must be same", ErrorCode.TABLE_VERSION_ADD_ERROR.getCode());
            throw new DSSDatamodelCenterException(ErrorCode.TABLE_VERSION_ADD_ERROR.getCode(), "table name must be same");
        }

        //查询字段信息
        List<DssDatamodelTableColumns> orgColumns = tableColumnsService.listByTableId(id);

        //备份旧版本
        tableVersionService.addOlderVersion(orgVersion, orgColumns);

        //删除旧版本
        getBaseMapper().deleteById(id);
        tableColumnsService.deleteByTableId(id);

        DssDatamodelTable newOne = modelMapper.map(vo, DssDatamodelTable.class);
        newOne.setVersion(assignVersion);
        newOne.setCreateTime(new Date());
        newOne.setUpdateTime(new Date());
        getBaseMapper().insert(newOne);

        Long tableId = newOne.getId();
        List<DssDatamodelTableColumns> newColumns = addColumns(tableId, vo.getColumns());

        //发布更新模型绑定事件
        String user = DataModelSecurityContextHolder.getContext().getDataModelAuthentication().getUser();
        publisher.publishEvent(new UpdateBindModelByTableEvent(this, user, orgVersion, newOne));
        publisher.publishEvent(new UpdateBindModelByColumnsEvent(this, user, orgVersion.getName(), orgColumns, newColumns));
        publisher.publishEvent(new UpdateBindLabelByTableEvent(this, user, orgVersion, newOne));
        return 1;
    }

    private String newVersion(String orgName, String orgVersion) {
        String lastVersion = tableVersionService.findLastVersion(orgName);

        return StringUtils.isBlank(lastVersion) ?
                (Integer.parseInt(orgVersion) + 1 + "") :
                (Integer.parseInt(lastVersion) > Integer.parseInt(orgVersion)) ? (Integer.parseInt(lastVersion) + 1 + "") :
                        (Integer.parseInt(orgVersion) + 1 + "");
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer versionRollBack(TableVersionRollBackVO vo) throws ErrorException {

        String name = vo.getName();
        String version = vo.getVersion();

        //获取当前使用版本表
        DssDatamodelTable current =
                getBaseMapper().selectOne(Wrappers.<DssDatamodelTable>lambdaQuery().eq(DssDatamodelTable::getName, name));

        //判断当前使用表
        if (current == null) {
            LOGGER.error("errorCode : {}, current table not exists, name : {} version : {}", ErrorCode.TABLE_VERSION_ROLL_BACK_ERROR.getCode(), name, version);
            throw new DSSDatamodelCenterException(ErrorCode.TABLE_VERSION_ROLL_BACK_ERROR.getCode(), "current table not exists, name : " + name + " version : " + version);
        }

        //判断当前版本是否有数据
        tableMaterializedHistoryService.checkData(current, vo.getUser());
        //没有数据删除表
        //tableMaterializedHistoryService.dropTable(current.getName(),vo.getUser());

        //查询字段信息
        List<DssDatamodelTableColumns> currentColumns = tableColumnsService.listByTableId(current.getId());

        //当前版本备份
        tableVersionService.addOlderVersion(current, currentColumns);

        //删除当前版本
        getBaseMapper().deleteById(current.getId());
        tableColumnsService.deleteByTableId(current.getId());


        //获取需要回退指标版本信息
        DssDatamodelTableVersion rollBackVersion = tableVersionService.findBackup(name, version);
        if (rollBackVersion == null) {
            LOGGER.error("errorCode : {}, table name : {} version : {}", ErrorCode.TABLE_VERSION_ROLL_BACK_ERROR.getCode(), name, version);
            throw new DSSDatamodelCenterException(ErrorCode.TABLE_VERSION_ROLL_BACK_ERROR.getCode(), "table name : " + name + " version : " + version + " not exist");
        }

        Long rollbackId = rollBackVersion.getId();

        DssDatamodelTable rollbackOne = gson.fromJson(rollBackVersion.getTableParams(), DssDatamodelTable.class);
        rollbackOne.setId(null);
        getBaseMapper().insert(rollbackOne);

        List<DssDatamodelTableColumns> rollbackColumns = gson.fromJson(rollBackVersion.getColumns(), new TypeToken<List<DssDatamodelTableColumns>>() {
        }.getType());
        rollbackColumns.forEach(columns -> columns.setId(null));
        tableColumnsService.batchInsert(rollbackOne.getId(), rollbackColumns);
        tableVersionService.getBaseMapper().deleteById(rollbackId);


        //发布更新模型绑定事件
        String user = DataModelSecurityContextHolder.getContext().getDataModelAuthentication().getUser();
        publisher.publishEvent(new UpdateBindModelByTableEvent(this, user, current, rollbackOne));
        publisher.publishEvent(new UpdateBindModelByColumnsEvent(this, user, current.getName(), currentColumns, rollbackColumns));
        publisher.publishEvent(new UpdateBindLabelByTableEvent(this, user, current, rollbackOne));

        return 1;
    }


    @Override
    public Message listTableVersions(TableVersionQueryVO vo) {
        List<TableVersionQueryDTO> list = tableVersionService.getBaseMapper()
                .selectList(Wrappers.<DssDatamodelTableVersion>lambdaQuery().eq(DssDatamodelTableVersion::getName, vo.getName()))
                .stream()
                .map(dssDatamodelTableVersion -> modelMapper.map(dssDatamodelTableVersion, TableVersionQueryDTO.class))
                .collect(Collectors.toList());
        return Message.ok().data("list", list);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer tableCollect(TableCollectVO vo) throws ErrorException {
        if (vo.getTableId() != null) {
            DssDatamodelTable collection = getBaseMapper().selectById(vo.getTableId());
            if (collection == null) {
                LOGGER.error("errorCode : {}, collect table not exists id : {} ", ErrorCode.TABLE_COLLECT_ADD_ERROR.getCode(), vo.getTableId());
                throw new DSSDatamodelCenterException(ErrorCode.TABLE_COLLECT_ADD_ERROR.getCode(), " collect table not exist id : " + vo.getTableId());
            }
            return tableCollectService.tableCollect(vo.getUser(), collection);
        }

        return tableCollectService.tableCollect(vo);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer tableCancel(TableCollectCancelVO vo) throws ErrorException {
        return tableCollectService.tableCollectCancel(vo.getUser(), vo.getTableName());
    }


    @Override
    public Message tableCollections(TableCollectQueryVO vo) throws ErrorException {
        QueryWrapper<DssDatamodelTableCollcetion> queryWrapper = new QueryWrapper<DssDatamodelTableCollcetion>()
                .like(StringUtils.isNotBlank(vo.getName()), "name", vo.getName())
                .like(StringUtils.isNotBlank(vo.getDataBase()), "database", vo.getName())
                .eq(StringUtils.isNotBlank(vo.getUser()), "user", vo.getUser())
                .eq(StringUtils.isNotBlank(vo.getWarehouseLayerName()), "warehouse_layer_name", vo.getWarehouseLayerName())
                .eq(StringUtils.isNotBlank(vo.getWarehouseThemeName()), "warehouse_theme_name", vo.getWarehouseThemeName());
        PageHelper.clearPage();
        PageHelper.startPage(vo.getPageNum(), vo.getPageSize());
        PageInfo<DssDatamodelTableCollcetion> pageInfo = new PageInfo<>(tableCollectService.getBaseMapper().selectList(queryWrapper));

        return Message.ok()
                .data("list", pageInfo.getList().stream().map(entity -> {
                    TableCollectionDTO dto = modelMapper.map(entity, TableCollectionDTO.class);
                    //收藏表的Id 不返回前端
                    dto.setId(null);
                    return dto;
                }).collect(Collectors.toList()))
                .data("total", pageInfo.getTotal());
    }


    @Override
    public Message dictionaryList(TableDictionaryListVO vo) {
        return Message.ok().data("list", tableDictionaryService.listByType(vo.getType()));
    }


    @Override
    public Integer addTableColumn(TableColumnsAddVO vo) throws ErrorException {
        int count = getBaseMapper().selectCount(Wrappers.<DssDatamodelTable>lambdaQuery().eq(DssDatamodelTable::getId, vo.getTableId()));
        if (count < 1) {
            LOGGER.error("errorCode : {},  table not exists id : {} ", ErrorCode.TABLE_COLUMN_ADD_ERROR.getCode(), vo.getTableId());
            throw new DSSDatamodelCenterException(ErrorCode.TABLE_COLUMN_ADD_ERROR.getCode(), " table not exists id : " + vo.getTableId());
        }
        DssDatamodelTableColumns newColumn = modelMapper.map(vo, DssDatamodelTableColumns.class);
        return tableColumnsService.addColumn(newColumn);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer tableColumnBind(Long columnId, TableColumnBindVO vo) throws ErrorException {
        return tableColumnsService.tableColumnBind(columnId, vo.getModelType(), vo.getModelName(), vo.getModelNameEn());
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer tableCreate(TableCreateVO vo) throws ErrorException {
        DssDatamodelTable current = getBaseMapper().selectById(vo.getTableId());
        if (current == null) {
            LOGGER.error("errorCode : {},  table not exists id : {} ", ErrorCode.TABLE_CREATE_ERROR.getCode(), vo.getTableId());
            throw new DSSDatamodelCenterException(ErrorCode.TABLE_CREATE_ERROR.getCode(), " table not exists id : " + vo.getTableId());
        }
        tableMaterializedHistoryService.materializedTable(current, vo.getUser());
        //发布尝试表绑定模型事件
        publisher.publishEvent(new TableFirstBindEvent(this
                ,DataModelSecurityContextHolder.getContext().getDataModelAuthentication().getUser()
                ,current.getId()
                ,current.getName()));
        return 1;
    }




    @Override
    public String tableCreateSql(TableCreateSqlVO vo) throws ErrorException {

        //先查询hdfs
        if (StringUtils.isNotBlank(vo.getGuid())) {
            GetHiveTblCreateResult result = linkisDataAssetsRemoteClient.getHiveTblCreate(GetHiveTblCreateAction.builder().setUser(vo.getUser()).setGuid(vo.getGuid()).build());
            if (StringUtils.isNotBlank(result.getResult())) {
                LOGGER.info("sql : {}", result.getResult());
                return result.getResult();
            }
        }

        DssDatamodelTable current = getBaseMapper().selectById(vo.getTableId());
        if (current == null) {
            LOGGER.error("errorCode : {},  table not exists id : {} ", ErrorCode.TABLE_CREATE_SQL_ERROR.getCode(), vo.getTableId());
            throw new DSSDatamodelCenterException(ErrorCode.TABLE_CREATE_SQL_ERROR.getCode(), " table not exists id : " + vo.getTableId());
        }
        String sql = tableMaterializedHistoryService.generateSql(current);
        LOGGER.info("sql : {}", sql);
        return sql;
    }


    @Override
    public Message list(TableListVO vo) {
        if (vo.getModelType() > 0) {
            return listByIndicator(vo);
        }

        if (vo.getTableType() < 0 || StringUtils.isNotBlank(vo.getWarehouseLayerName()) || StringUtils.isNotBlank(vo.getWarehouseThemeName())) {
            return listDataModel(vo);
        }
        return listAssets(vo);
    }

    private Message listByIndicator(TableListVO vo) {
        PageHelper.clearPage();
        PageHelper.startPage(vo.getPageNum(), vo.getPageSize());

        PageInfo<DssDatamodelTabelQuery> pageInfo = new PageInfo<>(tableQueryMapper.page(Wrappers.<DssDatamodelTabelQuery>lambdaQuery()
                .eq(DssDatamodelTabelQuery::getModelType, vo.getModelType())
                .like(StringUtils.isNotBlank(vo.getModelName()), DssDatamodelTabelQuery::getModelName, vo.getModelName())
                .eq(StringUtils.isNotBlank(vo.getWarehouseLayerName()), DssDatamodelTabelQuery::getWarehouseLayerName, vo.getWarehouseLayerName())
                .eq(StringUtils.isNotBlank(vo.getWarehouseThemeName()), DssDatamodelTabelQuery::getWarehouseThemeName, vo.getWarehouseThemeName())
                .groupBy(DssDatamodelTabelQuery::getId)));
        return Message.ok().data("list", pageInfo.getList().stream().map(entity -> modelMapper.map(entity, TableListDTO.class)).collect(Collectors.toList()))
                .data("total", pageInfo.getTotal());
    }

    private Message listAssets(TableListVO vo) {
        SearchHiveTblResult result = linkisDataAssetsRemoteClient.searchHiveTbl(SearchHiveTblAction.builder()
                .setUser(vo.getUser())
                .setQuery(vo.getName())
                .setOffset((vo.getPageNum() - 1) * vo.getPageSize())
                .setLimit(vo.getPageSize()).build());
        List<HiveTblSimpleInfoDTO> dtos = assertsGson.fromJson(assertsGson.toJson(result.getResult()), new TypeToken<List<HiveTblSimpleInfoDTO>>() {
        }.getType());
        if (CollectionUtils.isEmpty(dtos)) {
            return Message.ok().data("list", Lists.newArrayList());
        }
        List<TableListDTO> tableListDTOS = Lists.newArrayList();
        dtos.forEach(hiveTblSimpleInfoDTO -> {
            TableListDTO tableListDTO = new TableListDTO();
            tableListDTO.setGuid(hiveTblSimpleInfoDTO.getGuid());
            tableListDTO.setCreator(hiveTblSimpleInfoDTO.getOwner());
            tableListDTO.setCreateTime(hiveTblSimpleInfoDTO.getCreateTime());
            tableListDTO.setName(StringUtils.substringBefore(hiveTblSimpleInfoDTO.getQualifiedName(), "@"));
            tableListDTO.setDataBase(StringUtils.substringBefore(hiveTblSimpleInfoDTO.getQualifiedName(), "."));

            tableListDTO.setUpdateTime(hiveTblSimpleInfoDTO.getLastAccessTime());
            tableListDTO.setAlias(hiveTblSimpleInfoDTO.getAliases());
            tableListDTO.setComment(hiveTblSimpleInfoDTO.getComment());
            tableListDTO.setSize(getSize(hiveTblSimpleInfoDTO));
            String theme = getThemes(hiveTblSimpleInfoDTO.getClassifications());
            tableListDTO.setWarehouseThemeName(theme);
            tableListDTO.setWarehouseThemeNameEn(theme);
            String layer = getLayer(hiveTblSimpleInfoDTO.getClassifications());
            tableListDTO.setWarehouseLayerName(layer);
            tableListDTO.setWarehouseLayerNameEn(layer);

            tableListDTOS.add(tableListDTO);
        });

        return Message.ok().data("list", tableListDTOS)
                .data("total", tableListDTOS.size());
    }

    private String getThemes(List<String> classifications) {
        return getModel(ClassificationConstant.THEME,classifications);
    }
    private String getLayer(List<String> classifications) {
        return getModel(ClassificationConstant.LAYER,classifications);
    }

    private String getModel(ClassificationConstant classificationConstant,List<String> classifications) {
        if (CollectionUtils.isEmpty(classifications)) {
            return null;
        }
        for (String classification : classifications) {
            String prefix = StringUtils.substringBefore(classification, ClassificationConstant.SEPARATOR);
            if (classificationConstant.getTypeCode().equals(prefix)) {
                return StringUtils.substringAfter(classification, ClassificationConstant.SEPARATOR);
            }
        }
        return null;
    }

    private long getSize(HiveTblSimpleInfoDTO hiveTblSimpleInfoDTO) {
        long size = 0L;
        if (StringUtils.isBlank(hiveTblSimpleInfoDTO.getTotalSize())) {
            return size;
        }
        try {
            size = Long.parseLong(hiveTblSimpleInfoDTO.getTotalSize());
        } catch (Exception e) {
            //ignore
        }
        return size;
    }

    private Message listDataModel(TableListVO vo) {
        PageHelper.clearPage();
        PageHelper.startPage(vo.getPageNum(), vo.getPageSize());
        PageInfo<DssDatamodelTable> pageInfo = new PageInfo<>(getBaseMapper().selectList(Wrappers.<DssDatamodelTable>lambdaQuery()
                .eq(StringUtils.isNotBlank(vo.getWarehouseThemeName()), DssDatamodelTable::getWarehouseThemeName, vo.getWarehouseThemeName())
                .eq(StringUtils.isNotBlank(vo.getWarehouseLayerName()), DssDatamodelTable::getWarehouseLayerName, vo.getWarehouseLayerName())
                .like(StringUtils.isNotBlank(vo.getName()), DssDatamodelTable::getName, vo.getName())));
        return Message.ok().data("list", pageInfo.getList().stream().map(entity -> modelMapper.map(entity, TableListDTO.class)).collect(Collectors.toList()))
                .data("total", pageInfo.getTotal());
    }

    @Override
    public Message listTablePartitionStats(TblPartitionStatsVO vo) {
        if (StringUtils.isNotBlank(vo.getGuid())) {
            return queryByGuid(vo.getGuid(), vo.getUser());
        }
        SearchHiveTblResult result = linkisDataAssetsRemoteClient.searchHiveTbl(SearchHiveTblAction.builder().setUser(vo.getUser()).setPrecise(QueryType.PRECISE).setQuery(vo.getName()).setOffset(0).setLimit(1).build());
        List<HiveTblSimpleInfoDTO> dtos = assertsGson.fromJson(assertsGson.toJson(result.getResult()), new TypeToken<List<HiveTblSimpleInfoDTO>>() {
        }.getType());
        if (CollectionUtils.isEmpty(dtos)) {
            return Message.ok().data("list", Lists.newArrayList());
        }
        return queryByGuid(dtos.get(0).getGuid(), vo.getUser());
    }

    @Override
    public Message listDataBases(TableDatabasesQueryVO vo) {
        SearchHiveDbResult result = linkisDataAssetsRemoteClient.searchHiveDb(
                SearchHiveDbAction.builder().setQuery(vo.getName()).setUser(vo.getUser()).setOffset(vo.getPageNum() - 1).setLimit(vo.getPageSize() * (vo.getPageNum() - 1)).build());
        return Message.ok().data("list", result.getResult());
    }


    @Override
    public Message previewData(TableDataPreviewVO vo) throws ErrorException {
        if (!tableMaterializedHistoryService.tableExists(vo.getTableName(), vo.getUser())) {
            return Message.ok();
        }
        DataModelUJESJobTask dataModelUJESJobTask = PreviewDataModelUJESJobTask.newBuilder().user(vo.getUser()).code(vo.getTableName()).count(10).build();
        PreviewDataDTO previewDataDTO = null;
        try {
            previewDataDTO = previewDataModelUJESJobLauncher.launch(dataModelUJESJobTask);
        } catch (Exception e) {
            if (e instanceof UJESJobException) {
                UJESJobException ujesJobException = (UJESJobException) e;
                //表不存在错误忽略
                if (ujesJobException.getErrCode() == 40002) {
                    LOGGER.error(e.getMessage(), e);
                    return Message.ok();
                }
            }
            throw e;
        }
        return Message.ok().data("detail", previewDataDTO);
    }


    @Override
    public Integer tableCheckData(TableCheckDataVO vo) throws ErrorException {
        return tableMaterializedHistoryService.hasData(vo.getTableName(), vo.getUser()) ? 1 : 0;
    }

    private Message queryByGuid(String guid, String user) {
        GetHiveTblPartitionResult result = linkisDataAssetsRemoteClient.getHiveTblPartition(GetHiveTblPartitionAction.builder().setUser(user).setGuid(guid).build());
        if (result.getResult() == null) {
            return Message.ok().data("list", Lists.newArrayList());
        }
        List<PartInfoDTO> partInfoDTOS = assertsGson.fromJson(assertsGson.toJson(result.getResult()), new TypeToken<List<PartInfoDTO>>() {
        }.getType());
        return Message.ok().data("list", partInfoDTOS);
    }


    @Override
    public int tableThemeReferenceCount(String name) {
        int currentCount = getBaseMapper().selectCount(Wrappers.<DssDatamodelTable>lambdaQuery().eq(DssDatamodelTable::getWarehouseThemeName, name));
        int currentCountEn = getBaseMapper().selectCount(Wrappers.<DssDatamodelTable>lambdaQuery().eq(DssDatamodelTable::getWarehouseThemeNameEn, name));
       // int versionCount = tableVersionService.tableContentReference(name);

        List<DssDatamodelTableVersion> preReferences = tableVersionService.tableContentMultipleReference(name);
        int versionCount = (int) preReferences.stream().filter(e->{
            DssDatamodelTable temp = gson.fromJson(e.getTableParams(),DssDatamodelTable.class);
            return StringUtils.equals(temp.getWarehouseThemeName(),name)||StringUtils.equals(temp.getWarehouseThemeNameEn(),name);
        }).count();

        return currentCount + versionCount + currentCountEn;
    }

    @Override
    public int tableLayerReferenceCount(String name) {
        int currentCount = getBaseMapper().selectCount(Wrappers.<DssDatamodelTable>lambdaQuery().eq(DssDatamodelTable::getWarehouseLayerName, name));
        int currentCountEn = getBaseMapper().selectCount(Wrappers.<DssDatamodelTable>lambdaQuery().eq(DssDatamodelTable::getWarehouseLayerNameEn, name));
        //int versionCount = tableVersionService.tableContentReference(name);
        List<DssDatamodelTableVersion> preReferences = tableVersionService.tableContentMultipleReference(name);
        int versionCount = (int) preReferences.stream().filter(e->{
            DssDatamodelTable temp = gson.fromJson(e.getTableParams(),DssDatamodelTable.class);
            return StringUtils.equals(temp.getWarehouseLayerName(),name)||StringUtils.equals(temp.getWarehouseLayerNameEn(),name);
        }).count();
        return currentCount + versionCount + currentCountEn;
    }

    @Override
    public int tableCycleReferenceCount(String name) {
        int currentCount = getBaseMapper().selectCount(Wrappers.<DssDatamodelTable>lambdaQuery().eq(DssDatamodelTable::getLifecycle, name));
        int currentCountEn = getBaseMapper().selectCount(Wrappers.<DssDatamodelTable>lambdaQuery().eq(DssDatamodelTable::getLifecycleEn, name));
        List<DssDatamodelTableVersion> preReferences = tableVersionService.tableContentMultipleReference(name);
        int versionCount = (int) preReferences.stream().filter(e->{
            DssDatamodelTable temp = gson.fromJson(e.getTableParams(),DssDatamodelTable.class);
            return StringUtils.equals(temp.getLifecycle(),name)||StringUtils.equals(temp.getLifecycleEn(),name);
        }).count();
        return currentCount + versionCount + currentCountEn;
    }

    @Override
    public int tableDimensionReferenceCount(String name) {
        return referenceCount(name, ModeType.DIMENSION);
    }

    private int referenceCount(String name, ModeType modeType) {
        int currentCount = tableColumnsService.modelReferenceCount(modeType, name);
        int currentCountEn = tableColumnsService.modelReferenceCountEn(modeType, name);
        //int versionCount = tableVersionService.tableColumnsReference(name);

        List<DssDatamodelTableVersion> preReferences = tableVersionService.tableColumnsReference(name);
        int versionCount = (int) preReferences.stream().filter(e->{
            DssDatamodelTableColumns temp = gson.fromJson(e.getTableParams(),DssDatamodelTableColumns.class);
            //名称相同且类型相同
            return modeType.getType() == temp.getModelType()&&StringUtils.equals(name,temp.getModelName());
        }).count();
        return currentCount + versionCount + currentCountEn;
    }

    @Override
    public int tableMeasureReferenceCount(String name) {
        return referenceCount(name, ModeType.MEASURE);
    }

    @Override
    public int tableIndicatorReferenceCount(String name) {
        return referenceCount(name, ModeType.INDICATOR);
    }

    @Override
    public int tableModifierReferenceCount(String name) {
        return 0;
    }


    @Override
    public void bind(long id, String user) throws ErrorException {
        DssDatamodelTable current = getBaseMapper().selectById(id);
        if (current == null) {
            LOGGER.error("errorCode : {}, bind table error not exists", ErrorCode.TABLE_BIND_ERROR.getCode());
            throw new DSSDatamodelCenterException(ErrorCode.TABLE_BIND_ERROR.getCode(), "bind table error not exists");
        }

        List<DssDatamodelTableColumns> currentColumns = tableColumnsService.listByTableId(id);
        publisher.publishEvent(new BindModelByTableEvent(this, user, current));
        publisher.publishEvent(new BindModelByColumnsEvent(this, user, current.getName(), currentColumns));
        publisher.publishEvent(new BindLabelByTableEvent(this, user, current));
    }

    @Override
    public void tryBind(long id) throws ErrorException {
        DssDatamodelTable current = getBaseMapper().selectById(id);
        if (current == null) {
            LOGGER.error("errorCode : {},  table not exists id : {} ", ErrorCode.TABLE_BIND_ERROR.getCode(), id);
            throw new DSSDatamodelCenterException(ErrorCode.TABLE_BIND_ERROR.getCode(), " table not exists id : " + id);
        }

        //发布尝试表绑定模型事件
        publisher.publishEvent(new TableFirstBindEvent(this
                ,DataModelSecurityContextHolder.getContext().getDataModelAuthentication().getUser()
                ,current.getId()
                ,current.getName()));
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteTable(Long id) throws ErrorException {
        DssDatamodelTable current = getBaseMapper().selectById(id);
        if (current == null) {
            return 0;
        }
        //有数据则不能删除
        if (tableMaterializedHistoryService.hasData(current.getName(), DataModelSecurityContextHolder.getContext().getDataModelAuthentication().getUser())) {
            LOGGER.error("errorCode : {},  table id : {} has data", ErrorCode.TABLE_DELETE_ERROR.getCode(), id);
            throw new DSSDatamodelCenterException(ErrorCode.TABLE_DELETE_ERROR.getCode(), "bind table id " + id + "has  data");
        }
        List<DssDatamodelTableColumns> columns = tableColumnsService.listByTableId(id);

        String user = DataModelSecurityContextHolder.getContext().getDataModelAuthentication().getUser();

        //删除表相关内容
        getBaseMapper().deleteById(id);
        //删除字段
        tableColumnsService.deleteByTableId(id);
        //删除版本
        tableVersionService.getBaseMapper().delete(Wrappers.<DssDatamodelTableVersion>lambdaQuery().eq(DssDatamodelTableVersion::getName, current.getName()));
        //删除收藏信息
        tableCollectService.getBaseMapper().delete(Wrappers.<DssDatamodelTableCollcetion>lambdaQuery().eq(DssDatamodelTableCollcetion::getName, current.getName()));
        //删除物化信息
        tableMaterializedHistoryService.getBaseMapper().delete(Wrappers.<DssDatamodelTableMaterializedHistory>lambdaQuery().eq(DssDatamodelTableMaterializedHistory::getTablename, current.getName()));
        //发布表解绑模型事件
        publisher.publishEvent(new UnBindModelByTableEvent(this, user, current));
        publisher.publishEvent(new UnBindModelByColumnsEvent(this, user, current.getName(), columns));

        return 1;
    }


    @Override
    public int tableLabelReferenceCount(String name) {
        int currentCount = getBaseMapper().selectCount(Wrappers.<DssDatamodelTable>lambdaQuery()
                .eq(DssDatamodelTable::getLabel, name)
                .or()
                .like(DssDatamodelTable::getLabel, name + ",")
                .or()
                .like(DssDatamodelTable::getLabel, "," + name + ",")
                .or()
                .like(DssDatamodelTable::getLabel, "," + name));

        List<DssDatamodelTableVersion> preReferences = tableVersionService.tableContentMultipleReference(name);
        int versionCount = (int) preReferences.stream().filter(e->{
            DssDatamodelTable temp = gson.fromJson(e.getTableParams(),DssDatamodelTable.class);
            return Sets.newHashSet(StringUtils.split(temp.getLabel(), LabelConstant.SEPARATOR)).contains(name);
        }).count();
        return currentCount + versionCount;
    }
}
