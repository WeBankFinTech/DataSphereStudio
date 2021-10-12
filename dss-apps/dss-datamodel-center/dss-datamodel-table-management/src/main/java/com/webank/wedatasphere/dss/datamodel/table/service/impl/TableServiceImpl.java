package com.webank.wedatasphere.dss.datamodel.table.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.webank.wedatasphere.dss.datamodel.center.common.constant.ErrorCode;
import com.webank.wedatasphere.dss.datamodel.center.common.exception.DSSDatamodelCenterException;
import com.webank.wedatasphere.dss.datamodel.table.dao.DssDatamodelTableMapper;
import com.webank.wedatasphere.dss.datamodel.table.dto.TableColumnQueryDTO;
import com.webank.wedatasphere.dss.datamodel.table.dto.TableQueryDTO;
import com.webank.wedatasphere.dss.datamodel.table.dto.TableStatsDTO;
import com.webank.wedatasphere.dss.datamodel.table.entity.DssDatamodelTable;
import com.webank.wedatasphere.dss.datamodel.table.entity.DssDatamodelTableColumns;
import com.webank.wedatasphere.dss.datamodel.table.entity.DssDatamodelTableStats;
import com.webank.wedatasphere.dss.datamodel.table.service.TableColumnsService;
import com.webank.wedatasphere.dss.datamodel.table.service.TableService;
import com.webank.wedatasphere.dss.datamodel.table.service.TableStatsService;
import com.webank.wedatasphere.dss.datamodel.table.service.TableVersionService;
import com.webank.wedatasphere.dss.datamodel.table.vo.TableAddVO;
import com.webank.wedatasphere.dss.datamodel.table.vo.TableColumnVO;
import com.webank.wedatasphere.dss.datamodel.table.vo.TableQueryOneVO;
import com.webank.wedatasphere.dss.datamodel.table.vo.TableUpdateVO;
import com.webank.wedatasphere.linkis.common.exception.ErrorException;
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int addTable(TableAddVO vo) throws ErrorException {

        int sameCount = getBaseMapper().selectCount(Wrappers.<DssDatamodelTable>lambdaQuery().eq(DssDatamodelTable::getName, vo.getName()));
        if (sameCount > 0) {
            LOGGER.error("errorCode : {}, table name can not repeat, name : {}", ErrorCode.TABLE_ADD_ERROR.getCode(),vo.getName());
            throw new DSSDatamodelCenterException(ErrorCode.TABLE_ADD_ERROR.getCode(), "table name can not repeat");
        }

        DssDatamodelTable newOne = modelMapper.map(vo, DssDatamodelTable.class);
        newOne.setCreateTime(new Date());
        newOne.setUpdateTime(new Date());
        newOne.setVersion("1");
        getBaseMapper().insert(newOne);

        Long tableId = newOne.getId();
        List<DssDatamodelTableColumns> columns = Lists.newArrayList();
        for (TableColumnVO columnVO : vo.getColumns()) {
            DssDatamodelTableColumns newColumn = modelMapper.map(columnVO, DssDatamodelTableColumns.class);
            columns.add(newColumn);
        }

        tableColumnsService.batchInsert(tableId, columns);

        return 1;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateTable(Long id, TableUpdateVO vo) throws ErrorException {

        //todo 判断数据表是否有数据，是否已经物化

        DssDatamodelTable org = getBaseMapper().selectById(id);
        if (org == null) {
            LOGGER.error("errorCode : {}, update table error not exists",ErrorCode.TABLE_UPDATE_ERROR.getCode());
            throw new DSSDatamodelCenterException(ErrorCode.TABLE_UPDATE_ERROR.getCode(), "update table error not exists");
        }

        //当更新表名称时,存在其他指标名称同名或者当前指标名称已经存在版本信息，则不允许修改指标名称
        if (!StringUtils.equals(vo.getName(), org.getName())) {
            int repeat = getBaseMapper().selectCount(Wrappers.<DssDatamodelTable>lambdaQuery().eq(DssDatamodelTable::getName, vo.getName()));
            String lastVersion = tableVersionService.findLastVersion(org.getName());
            if (repeat > 0 || StringUtils.isNotBlank(lastVersion)) {
                LOGGER.error("errorCode : {}, table name can not repeat",ErrorCode.TABLE_UPDATE_ERROR.getCode());
                throw new DSSDatamodelCenterException(ErrorCode.TABLE_UPDATE_ERROR.getCode(), "table name can not repeat");
            }
        }

        Long orgId = org.getId();
        String version = org.getVersion();

        DssDatamodelTable updateOne = modelMapper.map(vo,DssDatamodelTable.class);
        updateOne.setUpdateTime(new Date());
        int result = getBaseMapper().update(updateOne, Wrappers.<DssDatamodelTable>lambdaUpdate().eq(DssDatamodelTable::getId, id));
        if (result != 1) {
            LOGGER.error("errorCode : {}, update table error not exists",ErrorCode.TABLE_UPDATE_ERROR.getCode());
            throw new DSSDatamodelCenterException(ErrorCode.TABLE_UPDATE_ERROR.getCode(), "update table error not exists");
        }
        List<DssDatamodelTableColumns> columns = Lists.newArrayList();
        for (TableColumnVO columnVO : vo.getColumns()) {
            DssDatamodelTableColumns newColumn = modelMapper.map(columnVO, DssDatamodelTableColumns.class);
            columns.add(newColumn);
        }

        return tableColumnsService.batchUpdate(orgId,columns);
    }


    @Override
    public TableQueryDTO queryByName(TableQueryOneVO vo) {
        DssDatamodelTable table = getBaseMapper().selectOne(Wrappers.<DssDatamodelTable>lambdaQuery().eq(DssDatamodelTable::getName,vo.getName()));
        if (table==null){
            return null;
        }
        return queryTable(table);
    }

    private TableQueryDTO queryTable(DssDatamodelTable table) {
        TableQueryDTO tableQueryDTO = modelMapper.map(table,TableQueryDTO.class);

        List<TableColumnQueryDTO> columnQueryDTOS = tableColumnsService.listByTableId(table.getId())
                .stream().map(column->modelMapper.map(column,TableColumnQueryDTO.class)).collect(Collectors.toList());
        tableQueryDTO.setColumns(columnQueryDTOS);

        DssDatamodelTableStats tableStats = tableStatsService.queryByTableName(table.getName());
        if (tableStats!=null){
            tableQueryDTO.setStats(modelMapper.map(tableStats, TableStatsDTO.class));
        }

        return tableQueryDTO;
    }


    @Override
    public TableQueryDTO queryById(Long id) {
        DssDatamodelTable table = getBaseMapper().selectOne(Wrappers.<DssDatamodelTable>lambdaQuery().eq(DssDatamodelTable::getId,id));
        if (table==null){
            return null;
        }
        return queryTable(table);
    }
}
