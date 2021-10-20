package com.webank.wedatasphere.dss.datamodel.table.service.impl;


import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.webank.wedatasphere.dss.datamodel.table.dao.DssDatamodelTableVersionMapper;
import com.webank.wedatasphere.dss.datamodel.table.entity.DssDatamodelTable;
import com.webank.wedatasphere.dss.datamodel.table.entity.DssDatamodelTableColumns;
import com.webank.wedatasphere.dss.datamodel.table.entity.DssDatamodelTableVersion;
import com.webank.wedatasphere.dss.datamodel.table.service.TableMaterializedHistoryService;
import com.webank.wedatasphere.dss.datamodel.table.service.TableVersionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class TableVersionServiceImpl extends ServiceImpl<DssDatamodelTableVersionMapper, DssDatamodelTableVersion> implements TableVersionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TableVersionServiceImpl.class);

    private Gson gson = new Gson();

    @Resource
    private TableMaterializedHistoryService tableMaterializedHistoryService;


    @Override
    public String findLastVersion(String name) {
        //查询当前表名称最大版本
        DssDatamodelTableVersion lastVersion = getBaseMapper().selectOne(
                Wrappers.<DssDatamodelTableVersion>lambdaQuery()
                        .eq(DssDatamodelTableVersion::getName, name)
                        .orderByDesc(DssDatamodelTableVersion::getVersion));

        return lastVersion != null ? lastVersion.getVersion() : null;
    }



    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addOlderVersion(DssDatamodelTable orgTable, List<DssDatamodelTableColumns> orgColumns) {
        DssDatamodelTableVersion version  = new DssDatamodelTableVersion();
        version.setTblId(orgTable.getId());
        version.setName(orgTable.getName());
        version.setComment(orgTable.getComment());
        version.setTableCode(tableMaterializedHistoryService.generateSql(orgTable));//todo 生成建表脚本
        version.setIsMaterialized(1);//todo 判断是否物化
        version.setColumns(gson.toJson(orgColumns));
        version.setTableParams(gson.toJson(orgTable));
        version.setSourceType("add");//todo 此字段的意义
        version.setCreateTime(new Date());
        version.setUpdateTime(new Date());
        version.setVersion(orgTable.getVersion());
        getBaseMapper().insert(version);
        return version.getId();
    }


    @Override
    public DssDatamodelTableVersion findBackup(String name, String version) {
        return getBaseMapper().selectOne(
                Wrappers.<DssDatamodelTableVersion>lambdaQuery()
                        .eq(DssDatamodelTableVersion::getName, name)
                        .eq(DssDatamodelTableVersion::getVersion, version));
    }
}
