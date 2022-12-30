package com.webank.wedatasphere.dss.datamodel.table.service.impl;


import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.webank.wedatasphere.dss.datamodel.table.dao.DssDatamodelTableCollcetionMapper;
import com.webank.wedatasphere.dss.datamodel.table.entity.DssDatamodelTable;
import com.webank.wedatasphere.dss.datamodel.table.entity.DssDatamodelTableCollcetion;
import com.webank.wedatasphere.dss.datamodel.table.service.TableCollectService;
import com.webank.wedatasphere.dss.datamodel.table.vo.TableCollectVO;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;

@Service
public class TableCollectServiceImpl extends ServiceImpl<DssDatamodelTableCollcetionMapper, DssDatamodelTableCollcetion> implements TableCollectService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TableCollectServiceImpl.class);

    @Resource
    private ModelMapper modelMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer tableCollect(String user, DssDatamodelTable targetTable) {
        cancelOlder(user,targetTable.getName());
        DssDatamodelTableCollcetion newCollect = modelMapper.map(targetTable, DssDatamodelTableCollcetion.class);
        newCollect.setCreateTime(new Date());
        newCollect.setUpdateTime(new Date());
        newCollect.setUser(user);
        getBaseMapper().insert(newCollect);
        return 1;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer tableCollect(TableCollectVO vo) {
        cancelOlder(vo.getUser(),vo.getName());
        DssDatamodelTableCollcetion newOne = modelMapper.map(vo, DssDatamodelTableCollcetion.class);
        newOne.setCreateTime(new Date());
        newOne.setUpdateTime(new Date());
        getBaseMapper().insert(newOne);
        return 1;
    }

    private Integer cancelOlder(String user, String tableName) {
        DssDatamodelTableCollcetion olderCollection = getBaseMapper().selectOne(Wrappers.<DssDatamodelTableCollcetion>lambdaQuery()
                .eq(DssDatamodelTableCollcetion::getName, tableName)
                .eq(DssDatamodelTableCollcetion::getUser, user));

        //如果已经收藏过该表则删除原有收藏,方便以后新增收藏
        if (olderCollection != null&&olderCollection.getId()!=null) {
            return getBaseMapper().deleteById(olderCollection.getId());
        }
        return 0;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer tableCollectCancel(String user, String tableName) {
        return cancelOlder(user, tableName);
    }
}
