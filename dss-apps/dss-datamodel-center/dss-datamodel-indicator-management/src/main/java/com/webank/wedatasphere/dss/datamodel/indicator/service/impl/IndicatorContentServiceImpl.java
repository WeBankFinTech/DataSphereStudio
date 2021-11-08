package com.webank.wedatasphere.dss.datamodel.indicator.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.webank.wedatasphere.dss.datamodel.center.common.constant.ErrorCode;
import com.webank.wedatasphere.dss.datamodel.center.common.exception.DSSDatamodelCenterException;
import com.webank.wedatasphere.dss.datamodel.indicator.dao.DssDatamodelIndicatorContentMapper;
import com.webank.wedatasphere.dss.datamodel.indicator.entity.DssDatamodelIndicatorContent;
import com.webank.wedatasphere.dss.datamodel.indicator.service.IndicatorContentService;
import com.webank.wedatasphere.dss.datamodel.indicator.vo.IndicatorContentVO;
import com.webank.wedatasphere.linkis.common.exception.ErrorException;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;


@Service
public class IndicatorContentServiceImpl extends ServiceImpl<DssDatamodelIndicatorContentMapper, DssDatamodelIndicatorContent> implements IndicatorContentService {

    private final Gson gson = new Gson();

    @Resource
    private ModelMapper modelMapper;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public int addIndicatorContent(Long indicatorId, String version, IndicatorContentVO vo) throws ErrorException {

        DssDatamodelIndicatorContent newOne = modelMapper.map(vo, DssDatamodelIndicatorContent.class);
        newOne.setVersion(version);
        newOne.setIndicatorId(indicatorId);
        //todo 检查内容
        newOne.setIndicatorSourceInfo(gson.toJson(vo.getSourceInfo()));
        newOne.setCreateTime(new Date());
        newOne.setUpdateTime(new Date());
        if (!save(newOne)) {
            throw new DSSDatamodelCenterException(ErrorCode.INDICATOR_CONTENT_ADD_ERROR.getCode(), "add indicator content error");
        }
        return 1;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateIndicatorContent(Long indicateId, String version, IndicatorContentVO vo) throws ErrorException {
        //删除原有详细信息
        getBaseMapper().delete(Wrappers.<DssDatamodelIndicatorContent>lambdaQuery().eq(DssDatamodelIndicatorContent::getIndicatorId, indicateId));
        //新增一条
        return addIndicatorContent(indicateId, version, vo);
    }


    @Override
    public DssDatamodelIndicatorContent queryByIndicateId(Long indicateId) {
        return getBaseMapper()
                .selectOne(Wrappers.<DssDatamodelIndicatorContent>lambdaQuery().eq(DssDatamodelIndicatorContent::getIndicatorId, indicateId));
    }


    @Override
    public int deleteByIndicatorId(Long id) throws ErrorException {
        return getBaseMapper().delete(Wrappers.<DssDatamodelIndicatorContent>lambdaQuery().eq(DssDatamodelIndicatorContent::getIndicatorId, id));
    }
}
