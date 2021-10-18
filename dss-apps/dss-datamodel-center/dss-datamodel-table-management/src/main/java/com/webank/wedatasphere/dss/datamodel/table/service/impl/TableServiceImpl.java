package com.webank.wedatasphere.dss.datamodel.table.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.webank.wedatasphere.dss.datamodel.center.common.constant.ErrorCode;
import com.webank.wedatasphere.dss.datamodel.center.common.exception.DSSDatamodelCenterException;
import com.webank.wedatasphere.dss.datamodel.table.dao.DssDatamodelTableMapper;
import com.webank.wedatasphere.dss.datamodel.table.dto.TableColumnQueryDTO;
import com.webank.wedatasphere.dss.datamodel.table.dto.TableListDTO;
import com.webank.wedatasphere.dss.datamodel.table.dto.TableQueryDTO;
import com.webank.wedatasphere.dss.datamodel.table.dto.TableStatsDTO;
import com.webank.wedatasphere.dss.datamodel.table.entity.*;
import com.webank.wedatasphere.dss.datamodel.table.service.*;
import com.webank.wedatasphere.dss.datamodel.table.vo.*;
import com.webank.wedatasphere.linkis.common.exception.ErrorException;
import com.webank.wedatasphere.linkis.server.Message;
import org.apache.commons.lang.StringUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    private Gson gson = new Gson();

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int addTable(TableAddVO vo) throws ErrorException {

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
        addColumns(tableId, vo.getColumns());

        return 1;
    }

    private void addColumns(Long tableId, List<TableColumnVO> columnVOs) throws ErrorException {
        List<DssDatamodelTableColumns> columns = Lists.newArrayList();
        for (TableColumnVO columnVO : columnVOs) {
            DssDatamodelTableColumns newColumn = modelMapper.map(columnVO, DssDatamodelTableColumns.class);
            columns.add(newColumn);
        }

        tableColumnsService.batchInsert(tableId, columns);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateTable(Long id, TableUpdateVO vo) throws ErrorException {

        //todo 判断数据表是否有数据

        DssDatamodelTable org = getBaseMapper().selectById(id);
        if (org == null) {
            LOGGER.error("errorCode : {}, update table error not exists", ErrorCode.TABLE_UPDATE_ERROR.getCode());
            throw new DSSDatamodelCenterException(ErrorCode.TABLE_UPDATE_ERROR.getCode(), "update table error not exists");
        }

        //当更新表名称时,存在其他指标名称同名或者当前指标名称已经存在版本信息，则不允许修改指标名称
        if (!StringUtils.equals(vo.getName(), org.getName())) {
            int repeat = getBaseMapper().selectCount(Wrappers.<DssDatamodelTable>lambdaQuery().eq(DssDatamodelTable::getName, vo.getName()));
            String lastVersion = tableVersionService.findLastVersion(org.getName());
            if (repeat > 0 || StringUtils.isNotBlank(lastVersion)) {
                LOGGER.error("errorCode : {}, table name can not repeat", ErrorCode.TABLE_UPDATE_ERROR.getCode());
                throw new DSSDatamodelCenterException(ErrorCode.TABLE_UPDATE_ERROR.getCode(), "table name can not repeat");
            }
        }

        Long orgId = org.getId();
        String version = org.getVersion();

        DssDatamodelTable updateOne = modelMapper.map(vo, DssDatamodelTable.class);
        updateOne.setUpdateTime(new Date());
        int result = getBaseMapper().update(updateOne, Wrappers.<DssDatamodelTable>lambdaUpdate().eq(DssDatamodelTable::getId, id));
        if (result != 1) {
            LOGGER.error("errorCode : {}, update table error not exists", ErrorCode.TABLE_UPDATE_ERROR.getCode());
            throw new DSSDatamodelCenterException(ErrorCode.TABLE_UPDATE_ERROR.getCode(), "update table error not exists");
        }
        List<DssDatamodelTableColumns> columns = Lists.newArrayList();
        for (TableColumnVO columnVO : vo.getColumns()) {
            DssDatamodelTableColumns newColumn = modelMapper.map(columnVO, DssDatamodelTableColumns.class);
            columns.add(newColumn);
        }

        return tableColumnsService.batchUpdate(orgId, columns);
    }


    @Override
    public TableQueryDTO queryByName(TableQueryOneVO vo) throws ErrorException{
        DssDatamodelTable table = getBaseMapper().selectOne(Wrappers.<DssDatamodelTable>lambdaQuery().eq(DssDatamodelTable::getName, vo.getName()));
        if (table == null) {
            LOGGER.error("errorCode : {}, table name : {} not exists", ErrorCode.TABLE_QUERY_ERROR.getCode(), vo.getName());
            throw new DSSDatamodelCenterException(ErrorCode.TABLE_QUERY_ERROR.getCode(), "table  name " + vo.getName() + " not exists");
        }
        return queryTable(table);
    }

    private TableQueryDTO queryTable(DssDatamodelTable table) {
        TableQueryDTO tableQueryDTO = modelMapper.map(table, TableQueryDTO.class);

        List<TableColumnQueryDTO> columnQueryDTOS = tableColumnsService.listByTableId(table.getId())
                .stream().map(column -> modelMapper.map(column, TableColumnQueryDTO.class)).collect(Collectors.toList());
        tableQueryDTO.setColumns(columnQueryDTOS);

        DssDatamodelTableStats tableStats = tableStatsService.queryByTableName(table.getName());
        if (tableStats != null) {
            tableQueryDTO.setStats(modelMapper.map(tableStats, TableStatsDTO.class));
        }

        return tableQueryDTO;
    }


    @Override
    public TableQueryDTO queryById(Long id) throws ErrorException{
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
        addColumns(tableId, vo.getColumns());


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

        DssDatamodelTable rollbackOne = gson.fromJson(rollBackVersion.getTableParams(), DssDatamodelTable.class);
        rollbackOne.setId(null);
        getBaseMapper().insert(rollbackOne);

        List<DssDatamodelTableColumns> rollbackColumns = gson.fromJson(rollBackVersion.getColumns(), new TypeToken<List<DssDatamodelTableColumns>>() {
        }.getType());
        rollbackColumns.forEach(columns -> columns.setId(null));
        tableColumnsService.batchInsert(rollbackOne.getId(), rollbackColumns);

        return 1;
    }


    @Override
    public Message listTableVersions(TableVersionQueryVO vo) {
        List<TableColumnQueryDTO> list = tableVersionService.getBaseMapper()
                .selectList(Wrappers.<DssDatamodelTableVersion>lambdaQuery().eq(DssDatamodelTableVersion::getName, vo.getName()))
                .stream()
                .map(dssDatamodelIndicatorVersion -> modelMapper.map(dssDatamodelIndicatorVersion, TableColumnQueryDTO.class))
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
                .data("list", pageInfo.getList().stream().map(entity -> modelMapper.map(entity, TableColumnQueryDTO.class)).collect(Collectors.toList()))
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
        return tableColumnsService.tableColumnBind(columnId, vo.getModelType(), vo.getModelName());
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer tableCreate(TableCreateVO vo) throws ErrorException {
        DssDatamodelTable current = getBaseMapper().selectById(vo.getTableId());
        if (current == null) {
            LOGGER.error("errorCode : {},  table not exists id : {} ", ErrorCode.TABLE_CREATE_ERROR.getCode(), vo.getTableId());
            throw new DSSDatamodelCenterException(ErrorCode.TABLE_CREATE_ERROR.getCode(), " table not exists id : " + vo.getTableId());
        }
        return tableMaterializedHistoryService.materializedTable(current);
    }


    @Override
    public String tableCreateSql(TableCreateSqlVO vo) throws ErrorException {

        //todo 首先查询资产已存在表生成的sql

        DssDatamodelTable current = getBaseMapper().selectById(vo.getTableId());
        if (current == null) {
            LOGGER.error("errorCode : {},  table not exists id : {} ", ErrorCode.TABLE_CREATE_SQL_ERROR.getCode(), vo.getTableId());
            throw new DSSDatamodelCenterException(ErrorCode.TABLE_CREATE_SQL_ERROR.getCode(), " table not exists id : " + vo.getTableId());
        }
        return tableMaterializedHistoryService.generateSql(current);
    }

    @Override
    public Message list(TableListVO vo) {

        if (StringUtils.isNotBlank(vo.getWarehouseLayerName())||StringUtils.isNotBlank(vo.getWarehouseThemeName())){
            PageHelper.clearPage();
            PageHelper.startPage(vo.getPageNum(), vo.getPageSize());
            PageInfo<DssDatamodelTable> pageInfo = new PageInfo<>(getBaseMapper().selectList(Wrappers.<DssDatamodelTable>lambdaQuery()
                    .eq(StringUtils.isNotBlank(vo.getWarehouseThemeName()), DssDatamodelTable::getWarehouseThemeName, vo.getWarehouseThemeName())
                    .eq(StringUtils.isNotBlank(vo.getWarehouseLayerName()), DssDatamodelTable::getWarehouseLayerName, vo.getWarehouseLayerName())
                    .like(StringUtils.isNotBlank(vo.getName()), DssDatamodelTable::getName, vo.getName())));
            return Message.ok().data("list", pageInfo.getList().stream().map(entity->modelMapper.map(entity, TableListDTO.class)).collect(Collectors.toList()))
                               .data("total",pageInfo.getTotal());
        }

        //todo 搜索资产
        return Message.ok();
    }
}
