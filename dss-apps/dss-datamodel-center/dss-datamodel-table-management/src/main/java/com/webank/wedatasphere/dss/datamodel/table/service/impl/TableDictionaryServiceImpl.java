package com.webank.wedatasphere.dss.datamodel.table.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.webank.wedatasphere.dss.datamodel.table.dao.DssDatamodelDictionaryMapper;
import com.webank.wedatasphere.dss.datamodel.table.entity.DssDatamodelDictionary;
import com.webank.wedatasphere.dss.datamodel.table.entity.DssDatamodelTableCollcetion;
import com.webank.wedatasphere.dss.datamodel.table.service.TableDictionaryService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TableDictionaryServiceImpl extends ServiceImpl<DssDatamodelDictionaryMapper, DssDatamodelDictionary> implements TableDictionaryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TableColumnsServiceImpl.class);

    @Override
    public List<DssDatamodelDictionary> listByType(String type) {
        return getBaseMapper().selectList(Wrappers.<DssDatamodelDictionary>lambdaQuery().eq(DssDatamodelDictionary::getType,type));
    }
}
