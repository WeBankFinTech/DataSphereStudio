package com.webank.wedatasphere.dss.datamodel.table.service.impl;


import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.webank.wedatasphere.dss.datamodel.table.dao.DssDatamodelTableStatsMapper;
import com.webank.wedatasphere.dss.datamodel.table.entity.DssDatamodelTableStats;
import com.webank.wedatasphere.dss.datamodel.table.service.TableStatsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TableStatsServiceImpl extends ServiceImpl<DssDatamodelTableStatsMapper, DssDatamodelTableStats> implements TableStatsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TableServiceImpl.class);


    @Override
    public DssDatamodelTableStats queryByTableName(String tableName) {
        return getBaseMapper().selectOne(Wrappers.<DssDatamodelTableStats>lambdaQuery().eq(DssDatamodelTableStats::getName,tableName));
    }
}
