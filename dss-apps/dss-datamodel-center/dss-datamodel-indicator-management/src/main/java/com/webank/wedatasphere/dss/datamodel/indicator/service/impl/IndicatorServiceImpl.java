package com.webank.wedatasphere.dss.datamodel.indicator.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.webank.wedatasphere.dss.datamodel.center.common.constant.ErrorCode;
import com.webank.wedatasphere.dss.datamodel.center.common.exception.DSSDatamodelCenterException;
import com.webank.wedatasphere.dss.datamodel.indicator.dao.DssDatamodelIndicatorMapper;
import com.webank.wedatasphere.dss.datamodel.indicator.dao.IndicatorQueryMapper;
import com.webank.wedatasphere.dss.datamodel.indicator.dto.IndicatorContentQueryDTO;
import com.webank.wedatasphere.dss.datamodel.indicator.dto.IndicatorQueryDTO;
import com.webank.wedatasphere.dss.datamodel.indicator.entity.DssDatamodelIndicator;
import com.webank.wedatasphere.dss.datamodel.indicator.entity.DssDatamodelIndicatorContent;
import com.webank.wedatasphere.dss.datamodel.indicator.entity.DssDatamodelIndicatorQuery;
import com.webank.wedatasphere.dss.datamodel.indicator.restful.IndicatorRestfulApi;
import com.webank.wedatasphere.dss.datamodel.indicator.service.IndicatorContentService;
import com.webank.wedatasphere.dss.datamodel.indicator.service.IndicatorService;
import com.webank.wedatasphere.dss.datamodel.indicator.vo.IndicatorAddVO;
import com.webank.wedatasphere.dss.datamodel.indicator.vo.IndicatorQueryVO;
import com.webank.wedatasphere.dss.datamodel.indicator.vo.IndicatorUpdateVO;
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

/**
 * @author helong
 * @date 2021/9/16
 */
@Service
public class IndicatorServiceImpl extends ServiceImpl<DssDatamodelIndicatorMapper, DssDatamodelIndicator> implements IndicatorService {

    private static final Logger LOGGER = LoggerFactory.getLogger(IndicatorRestfulApi.class);

    @Resource
    private ModelMapper modelMapper;

    @Resource
    private IndicatorContentService indicatorContentService;

    @Resource
    private IndicatorQueryMapper indicatorQueryMapper;

    @Override
    @Transactional
    public int addIndicator(IndicatorAddVO vo, String version) throws ErrorException {
        DssDatamodelIndicator newOne = modelMapper.map(vo, DssDatamodelIndicator.class);
        //基本信息中设置版本号
        newOne.setVersion(version);
        if (getBaseMapper().selectCount(Wrappers.<DssDatamodelIndicator>lambdaQuery().eq(DssDatamodelIndicator::getName, vo.getName())) > 0) {
            throw new DSSDatamodelCenterException(ErrorCode.INDICATOR_ADD_ERROR.getCode(), "indicator name can not repeat");
        }
        if (!save(newOne)) {
            throw new DSSDatamodelCenterException(ErrorCode.INDICATOR_ADD_ERROR.getCode(), "add indicator error");
        }
        return indicatorContentService.addIndicatorContent(newOne.getId(), version, vo.getContent());
    }


    @Override
    @Transactional
    public int updateIndicator(Long id, IndicatorUpdateVO vo) throws ErrorException {

        if (getBaseMapper().selectCount(Wrappers.<DssDatamodelIndicator>lambdaQuery().eq(DssDatamodelIndicator::getName, vo.getName())) > 0) {
            throw new DSSDatamodelCenterException(ErrorCode.INDICATOR_UPDATE_ERROR.getCode(), "indicator name can not repeat");
        }

        DssDatamodelIndicator org = getBaseMapper().selectById(id);
        if (org == null) {
            throw new DSSDatamodelCenterException(ErrorCode.INDICATOR_UPDATE_ERROR.getCode(), "update indicator error not exists");
        }
        Long orgId = org.getId();
        String verison = org.getVersion();

        //更新原有指标
        DssDatamodelIndicator updateOne = modelMapper.map(vo, DssDatamodelIndicator.class);
        updateOne.setUpdateTime(new Date());
        int result = getBaseMapper().update(updateOne, Wrappers.<DssDatamodelIndicator>lambdaUpdate().eq(DssDatamodelIndicator::getId, id));
        if (result != 1) {
            throw new DSSDatamodelCenterException(ErrorCode.INDICATOR_UPDATE_ERROR.getCode(), "update indicator error not exists");
        }

        return indicatorContentService.updateIndicatorContent(orgId, verison, vo.getContent());
    }


    @Override
    public int deleteIndicator(Long id) throws ErrorException {
        return 0;
    }


    @Override
    public Message listIndicators(IndicatorQueryVO vo) {
        QueryWrapper<DssDatamodelIndicatorQuery> queryWrapper = new QueryWrapper<DssDatamodelIndicatorQuery>()
                .like(StringUtils.isNotBlank(vo.getName()), "name", vo.getName())
                .eq(vo.getIsAvailable() != null, "is_available", vo.getIsAvailable())
                .like(StringUtils.isNotBlank(vo.getOwner()), "owner", vo.getOwner());
        IPage<DssDatamodelIndicatorQuery> iPage = indicatorQueryMapper.page(new Page<>(vo.getPageNum(), vo.getPageSize()), queryWrapper);

        return Message.ok()
                .data("list", iPage
                        .getRecords())
                .data("total", iPage.getTotal());
    }


    @Override
    public Message queryById(Long id) throws DSSDatamodelCenterException {
        DssDatamodelIndicator indicator = baseMapper.selectById(id);
        if (indicator == null) {
            throw new DSSDatamodelCenterException(ErrorCode.INDICATOR_QUERY_ERROR.getCode(), "indicator id " + id + " not exists");
        }

        DssDatamodelIndicatorContent content = indicatorContentService.queryByIndicateId(id);
        if (content == null) {
            throw new DSSDatamodelCenterException(ErrorCode.INDICATOR_QUERY_ERROR.getCode(), "indicator content id " + id + " not exists");
        }

        IndicatorQueryDTO indicatorQueryDTO = modelMapper.map(indicator, IndicatorQueryDTO.class);
        IndicatorContentQueryDTO indicatorContentQueryDTO = modelMapper.map(content, IndicatorContentQueryDTO.class);
        indicatorQueryDTO.setContent(indicatorContentQueryDTO);
        return Message.ok().data("detail",indicatorQueryDTO);
    }
}
