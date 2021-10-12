package com.webank.wedatasphere.dss.datamodel.table.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.webank.wedatasphere.dss.datamodel.table.dao.DssDatamodelTableMapper;
import com.webank.wedatasphere.dss.datamodel.table.entity.DssDatamodelTable;
import com.webank.wedatasphere.dss.datamodel.table.service.TableVersionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TableVersionServiceImpl extends ServiceImpl<DssDatamodelTableMapper, DssDatamodelTable> implements TableVersionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TableServiceImpl.class);

    @Override
    public String findLastVersion(String name) {
        return null;
    }
}
