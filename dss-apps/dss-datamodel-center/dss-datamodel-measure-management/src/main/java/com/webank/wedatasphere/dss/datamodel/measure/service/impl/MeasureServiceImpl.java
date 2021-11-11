package com.webank.wedatasphere.dss.datamodel.measure.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.webank.wedatasphere.dss.datamodel.center.common.constant.ErrorCode;
import com.webank.wedatasphere.dss.datamodel.center.common.exception.DSSDatamodelCenterException;
import com.webank.wedatasphere.dss.datamodel.center.common.service.MeasureIndicatorCheckService;
import com.webank.wedatasphere.dss.datamodel.center.common.service.MeasuredTableCheckService;
import com.webank.wedatasphere.dss.datamodel.measure.dao.DssDatamodelMeasureMapper;
import com.webank.wedatasphere.dss.datamodel.measure.dto.MeasureQueryDTO;
import com.webank.wedatasphere.dss.datamodel.measure.entity.DssDatamodelMeasure;
import com.webank.wedatasphere.dss.datamodel.measure.service.MeasureService;
import com.webank.wedatasphere.dss.datamodel.measure.vo.MeasureAddVO;
import com.webank.wedatasphere.dss.datamodel.measure.vo.MeasureEnableVO;
import com.webank.wedatasphere.dss.datamodel.measure.vo.MeasureQueryVO;
import com.webank.wedatasphere.dss.datamodel.measure.vo.MeasureUpdateVO;
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
import java.util.stream.Collectors;


@Service
public class MeasureServiceImpl extends ServiceImpl<DssDatamodelMeasureMapper, DssDatamodelMeasure>  implements MeasureService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MeasureServiceImpl.class);
    
    private final ModelMapper modelMapper = new ModelMapper();

    @Resource
    private MeasureIndicatorCheckService measureIndicatorCheckService;

    @Resource
    private MeasuredTableCheckService measuredTableCheckService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int addMeasure(MeasureAddVO vo) throws ErrorException{
        if (getBaseMapper().selectCount(Wrappers.<DssDatamodelMeasure>lambdaQuery().eq(DssDatamodelMeasure::getName, vo.getName())
                .or().eq(DssDatamodelMeasure::getFieldIdentifier,vo.getFieldIdentifier())) > 0) {
            LOGGER.error("errorCode : {}, measure name or field identifier can not repeat, name : {}", ErrorCode.INDICATOR_ADD_ERROR.getCode(), vo.getName());
            throw new DSSDatamodelCenterException(ErrorCode.MEASURE_ADD_ERROR.getCode(), "measure name or field identifier can not repeat");
        }
        DssDatamodelMeasure newOne = modelMapper.map(vo,DssDatamodelMeasure.class);
        newOne.setCreateTime(new Date());
        newOne.setUpdateTime(new Date());
        return getBaseMapper().insert(newOne);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public int enableMeasure(Long id , MeasureEnableVO vo) {
        DssDatamodelMeasure enableOne = new DssDatamodelMeasure();
        enableOne.setIsAvailable(vo.getIsAvailable());
        enableOne.setUpdateTime(new Date());
        return getBaseMapper().update(enableOne, Wrappers.<DssDatamodelMeasure>lambdaUpdate().eq(DssDatamodelMeasure::getId,id));
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateMeasure(Long id, MeasureUpdateVO vo) throws ErrorException{
        DssDatamodelMeasure org =modelMapper.map(vo,DssDatamodelMeasure.class);
        if (org == null) {
            LOGGER.error("errorCode : {}, update measure error not exists", ErrorCode.MEASURE_UPDATE_ERROR.getCode());
            throw new DSSDatamodelCenterException(ErrorCode.MEASURE_UPDATE_ERROR.getCode(), "update measure error not exists");
        }

        //当更新名称时
        if (!StringUtils.equals(vo.getName(), org.getName())) {
            int repeat = getBaseMapper().selectCount(Wrappers.<DssDatamodelMeasure>lambdaQuery().eq(DssDatamodelMeasure::getName, vo.getName()));

            if (repeat > 0 ||(measuredTableCheckService.referenceCase(org.getName()))||measureIndicatorCheckService.referenceCase(org.getName())) {
                LOGGER.error("errorCode : {}, measure name can not repeat", ErrorCode.MEASURE_UPDATE_ERROR.getCode());
                throw new DSSDatamodelCenterException(ErrorCode.MEASURE_UPDATE_ERROR.getCode(), "measure name can not repeat");
            }
        }

        //当更新标识时
        if (!StringUtils.equals(vo.getFieldIdentifier(), org.getFieldIdentifier())) {
            int repeat = getBaseMapper().selectCount(Wrappers.<DssDatamodelMeasure>lambdaQuery().eq(DssDatamodelMeasure::getFieldIdentifier, vo.getFieldIdentifier()));

            if (repeat > 0 ||(measureIndicatorCheckService.referenceEn(org.getFieldIdentifier()))||measureIndicatorCheckService.referenceEn(org.getFieldIdentifier())) {
                LOGGER.error("errorCode : {}, measure field identifier can not repeat", ErrorCode.MEASURE_UPDATE_ERROR.getCode());
                throw new DSSDatamodelCenterException(ErrorCode.MEASURE_UPDATE_ERROR.getCode(), "measure field identifier can not repeat");
            }
        }
        org.setUpdateTime(new Date());
        return getBaseMapper().update(org, Wrappers.<DssDatamodelMeasure>lambdaUpdate().eq(DssDatamodelMeasure::getId,id));
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteMeasure(Long id) throws ErrorException {
        DssDatamodelMeasure dssDatamodelMeasure = getBaseMapper().selectById(id);
        if (dssDatamodelMeasure == null){
            throw new DSSDatamodelCenterException(ErrorCode.MEASURE_DELETE_ERROR.getCode(), "measure id " +id +" not exists");
        }
        //校验引用情况
        if (measuredTableCheckService.referenceCase(dssDatamodelMeasure.getName())||measuredTableCheckService.referenceEn(dssDatamodelMeasure.getFieldIdentifier())
                ||measureIndicatorCheckService.referenceCase(dssDatamodelMeasure.getName())||measureIndicatorCheckService.referenceEn(dssDatamodelMeasure.getFieldIdentifier())){
            throw new DSSDatamodelCenterException(ErrorCode.MEASURE_DELETE_ERROR.getCode(), "measure id " +id +" has referenced");
        }
        return getBaseMapper().deleteById(id);
    }


    @Override
    public Message listMeasures(MeasureQueryVO vo) {
        QueryWrapper<DssDatamodelMeasure> queryWrapper = new QueryWrapper<DssDatamodelMeasure>()
                .like(StringUtils.isNotBlank(vo.getName()),"name",vo.getName())
                .eq(vo.getIsAvailable()!=null,"is_available",vo.getIsAvailable())
                .eq(StringUtils.isNotBlank(vo.getWarehouseThemeName()),"warehouse_theme_name",vo.getWarehouseThemeName())
                .like(StringUtils.isNotBlank(vo.getOwner()),"owner",vo.getOwner());
        PageHelper.clearPage();
        PageHelper.startPage(vo.getPageNum(),vo.getPageSize());
        PageInfo<DssDatamodelMeasure> pageInfo = new PageInfo<>(getBaseMapper().selectList(queryWrapper));
       //IPage<DssDatamodelMeasure> iPage = page(new Page<>(vo.getPageNum(),vo.getPageSize()),queryWrapper);
        return Message.ok()
                .data("list",pageInfo
                        .getList()
                        .stream()
                        .map(dssDatamodelMeasure -> modelMapper.map(dssDatamodelMeasure, MeasureQueryDTO.class))
                        .collect(Collectors.toList()))
                .data("total",pageInfo.getTotal());
    }


    @Override
    public MeasureQueryDTO queryById(Long id) throws DSSDatamodelCenterException {
        DssDatamodelMeasure dssDatamodelMeasure = getBaseMapper().selectById(id);
        if (dssDatamodelMeasure == null){
            throw new DSSDatamodelCenterException(ErrorCode.MEASURE_QUERY_ERROR.getCode(), "measure id " +id +" not exists");
        }
        return modelMapper.map(dssDatamodelMeasure,MeasureQueryDTO.class);
    }


    @Override
    public int measureThemeReferenceCount(String name) {
        int count = getBaseMapper().selectCount(Wrappers.<DssDatamodelMeasure>lambdaQuery().eq(DssDatamodelMeasure::getWarehouseThemeName,name));
        int countEn = getBaseMapper().selectCount(Wrappers.<DssDatamodelMeasure>lambdaQuery().eq(DssDatamodelMeasure::getWarehouseThemeName,name));
        return count + countEn;
    }
}
