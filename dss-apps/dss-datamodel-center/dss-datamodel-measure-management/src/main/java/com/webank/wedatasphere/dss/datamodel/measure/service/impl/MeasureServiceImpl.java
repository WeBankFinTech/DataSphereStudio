package com.webank.wedatasphere.dss.datamodel.measure.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.webank.wedatasphere.dss.data.governance.entity.ClassificationConstant;
import com.webank.wedatasphere.dss.data.governance.response.CreateModelTypeResult;
import com.webank.wedatasphere.dss.data.governance.response.UpdateModelTypeResult;
import com.webank.wedatasphere.dss.datamodel.center.common.constant.ErrorCode;
import com.webank.wedatasphere.dss.datamodel.center.common.context.DataModelSecurityContextHolder;
import com.webank.wedatasphere.dss.datamodel.center.common.event.CreateModelEvent;
import com.webank.wedatasphere.dss.datamodel.center.common.event.DeleteModelEvent;
import com.webank.wedatasphere.dss.datamodel.center.common.event.UpdateModelEvent;
import com.webank.wedatasphere.dss.datamodel.center.common.exception.DSSDatamodelCenterException;
import com.webank.wedatasphere.dss.datamodel.center.common.service.AssertsSyncService;
import com.webank.wedatasphere.dss.datamodel.center.common.service.DatamodelReferencService;
import com.webank.wedatasphere.dss.datamodel.measure.dao.DssDatamodelMeasureMapper;
import com.webank.wedatasphere.dss.datamodel.measure.dto.MeasureQueryDTO;
import com.webank.wedatasphere.dss.datamodel.measure.entity.DssDatamodelMeasure;
import com.webank.wedatasphere.dss.datamodel.measure.service.MeasureService;
import com.webank.wedatasphere.dss.datamodel.measure.vo.MeasureAddVO;
import com.webank.wedatasphere.dss.datamodel.measure.vo.MeasureEnableVO;
import com.webank.wedatasphere.dss.datamodel.measure.vo.MeasureQueryVO;
import com.webank.wedatasphere.dss.datamodel.measure.vo.MeasureUpdateVO;
import org.apache.linkis.common.exception.ErrorException;
import org.apache.linkis.server.Message;
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
public class MeasureServiceImpl extends ServiceImpl<DssDatamodelMeasureMapper, DssDatamodelMeasure> implements MeasureService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MeasureServiceImpl.class);

    private final ModelMapper modelMapper = new ModelMapper();



    @Resource
    private AssertsSyncService assertsSyncService;

    @Resource
    private DatamodelReferencService datamodelReferencService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long addMeasure(MeasureAddVO vo) throws ErrorException {
        if (getBaseMapper().selectCount(Wrappers.<DssDatamodelMeasure>lambdaQuery().eq(DssDatamodelMeasure::getName, vo.getName())
                .or().eq(DssDatamodelMeasure::getFieldIdentifier, vo.getFieldIdentifier())) > 0) {
            LOGGER.error("errorCode : {}, measure name or field identifier can not repeat, name : {}", ErrorCode.INDICATOR_ADD_ERROR.getCode(), vo.getName());
            throw new DSSDatamodelCenterException(ErrorCode.MEASURE_ADD_ERROR.getCode(), "measure name or field identifier can not repeat");
        }


        //校验引用情况
        if(datamodelReferencService.measureReferenceCount(vo.getName())>0
                ||datamodelReferencService.measureReferenceCount(vo.getFieldIdentifier())>0){
            LOGGER.error("errorCode : {}, measure name can not be referenced ", ErrorCode.MEASURE_UPDATE_ERROR.getCode());
            throw new DSSDatamodelCenterException(ErrorCode.MEASURE_UPDATE_ERROR.getCode(), "measure name can not be referenced");
        }

        DssDatamodelMeasure newOne = modelMapper.map(vo, DssDatamodelMeasure.class);
        newOne.setCreateTime(new Date());
        newOne.setUpdateTime(new Date());
        getBaseMapper().insert(newOne);
        //同步atlas
        CreateModelTypeResult result = assertsSyncService.syncCreateModel(
                new CreateModelEvent(this
                        , DataModelSecurityContextHolder.getContext().getDataModelAuthentication().getUser()
                        , vo.getFieldIdentifier()
                        , ClassificationConstant.MEASURE));
        return newOne.getId();
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public int enableMeasure(Long id, MeasureEnableVO vo) {
        DssDatamodelMeasure enableOne = new DssDatamodelMeasure();
        enableOne.setIsAvailable(vo.getIsAvailable());
        enableOne.setUpdateTime(new Date());
        return getBaseMapper().update(enableOne, Wrappers.<DssDatamodelMeasure>lambdaUpdate().eq(DssDatamodelMeasure::getId, id));
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateMeasure(Long id, MeasureUpdateVO vo) throws ErrorException {
        DssDatamodelMeasure org = getBaseMapper().selectById(id);
        if (org == null) {
            LOGGER.error("errorCode : {}, update measure error not exists", ErrorCode.MEASURE_UPDATE_ERROR.getCode());
            throw new DSSDatamodelCenterException(ErrorCode.MEASURE_UPDATE_ERROR.getCode(), "update measure error not exists");
        }

        //校验引用情况
        if(datamodelReferencService.measureReferenceCount(vo.getName())>0
                ||datamodelReferencService.measureReferenceCount(vo.getFieldIdentifier())>0
                ||datamodelReferencService.measureReferenceCount(org.getName())>0
                ||datamodelReferencService.measureReferenceCount(org.getFieldIdentifier())>0){
            LOGGER.error("errorCode : {}, measure name can not be referenced ", ErrorCode.MEASURE_UPDATE_ERROR.getCode());
            throw new DSSDatamodelCenterException(ErrorCode.MEASURE_UPDATE_ERROR.getCode(), "measure name can not be referenced");
        }

        //当更新名称时
        if (!StringUtils.equals(vo.getName(), org.getName())) {
            int repeat = getBaseMapper().selectCount(Wrappers.<DssDatamodelMeasure>lambdaQuery().eq(DssDatamodelMeasure::getName, vo.getName()));
            if (repeat > 0 ) {
                LOGGER.error("errorCode : {}, measure name can not repeat ", ErrorCode.MEASURE_UPDATE_ERROR.getCode());
                throw new DSSDatamodelCenterException(ErrorCode.MEASURE_UPDATE_ERROR.getCode(), "measure name can not repeat ");
            }
        }

        String orgFieldIdentifier = org.getFieldIdentifier();
        //当更新标识时
        if (!StringUtils.equals(vo.getFieldIdentifier(), orgFieldIdentifier)) {
            int repeat = getBaseMapper().selectCount(Wrappers.<DssDatamodelMeasure>lambdaQuery().eq(DssDatamodelMeasure::getFieldIdentifier, vo.getFieldIdentifier()));

            if (repeat > 0 || datamodelReferencService.measureReferenceCount(orgFieldIdentifier)>0) {
                LOGGER.error("errorCode : {}, measure field identifier can not repeat or referenced", ErrorCode.MEASURE_UPDATE_ERROR.getCode());
                throw new DSSDatamodelCenterException(ErrorCode.MEASURE_UPDATE_ERROR.getCode(), "measure field identifier can not repeat or referenced");
            }
        }
        DssDatamodelMeasure updateOne = modelMapper.map(vo, DssDatamodelMeasure.class);
        updateOne.setUpdateTime(new Date());
        getBaseMapper().update(updateOne, Wrappers.<DssDatamodelMeasure>lambdaUpdate().eq(DssDatamodelMeasure::getId, id));
        //同步atlas
        UpdateModelTypeResult updateModelTypeResult = assertsSyncService.syncUpdateModel(
                new UpdateModelEvent(this
                        , DataModelSecurityContextHolder.getContext().getDataModelAuthentication().getUser()
                        , vo.getFieldIdentifier()
                        , orgFieldIdentifier
                        , ClassificationConstant.MEASURE));
        return 1;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteMeasure(Long id) throws ErrorException {
        DssDatamodelMeasure dssDatamodelMeasure = getBaseMapper().selectById(id);
        if (dssDatamodelMeasure == null) {
            throw new DSSDatamodelCenterException(ErrorCode.MEASURE_DELETE_ERROR.getCode(), "measure id " + id + " not exists");
        }
        //校验引用情况
        if(datamodelReferencService.measureReferenceCount(dssDatamodelMeasure.getName())>0
                ||datamodelReferencService.measureReferenceCount(dssDatamodelMeasure.getFieldIdentifier())>0){
            throw new DSSDatamodelCenterException(ErrorCode.MEASURE_DELETE_ERROR.getCode(), "measure id " + id + " has referenced");
        }

        getBaseMapper().deleteById(id);
        //同步资产
        assertsSyncService.syncDeleteModel(new DeleteModelEvent(this
                , DataModelSecurityContextHolder.getContext().getDataModelAuthentication().getUser()
                , dssDatamodelMeasure.getFieldIdentifier()
                , ClassificationConstant.MEASURE));
        return 1;
    }


    @Override
    public Message listMeasures(MeasureQueryVO vo) {
        QueryWrapper<DssDatamodelMeasure> queryWrapper = new QueryWrapper<DssDatamodelMeasure>()
                .like(StringUtils.isNotBlank(vo.getName()), "name", vo.getName())
                .eq(vo.getIsAvailable() != null, "is_available", vo.getIsAvailable())
                .eq(StringUtils.isNotBlank(vo.getWarehouseThemeName()), "warehouse_theme_name", vo.getWarehouseThemeName())
                .like(StringUtils.isNotBlank(vo.getOwner()), "owner", vo.getOwner());
        PageHelper.clearPage();
        PageHelper.startPage(vo.getPageNum(), vo.getPageSize());
        PageInfo<DssDatamodelMeasure> pageInfo = new PageInfo<>(getBaseMapper().selectList(queryWrapper));
        //IPage<DssDatamodelMeasure> iPage = page(new Page<>(vo.getPageNum(),vo.getPageSize()),queryWrapper);
        return Message.ok()
                .data("list", pageInfo
                        .getList()
                        .stream()
                        .map(dssDatamodelMeasure -> {
                            MeasureQueryDTO dto = modelMapper.map(dssDatamodelMeasure, MeasureQueryDTO.class);
                            dto.setRefCount(datamodelReferencService.measureReferenceCount(dssDatamodelMeasure.getName()));
                            return dto;
                        })
                        .collect(Collectors.toList()))
                .data("total", pageInfo.getTotal());
    }


    @Override
    public MeasureQueryDTO queryById(Long id) throws DSSDatamodelCenterException {
        DssDatamodelMeasure dssDatamodelMeasure = getBaseMapper().selectById(id);
        if (dssDatamodelMeasure == null) {
            throw new DSSDatamodelCenterException(ErrorCode.MEASURE_QUERY_ERROR.getCode(), "measure id " + id + " not exists");
        }
        MeasureQueryDTO dto = modelMapper.map(dssDatamodelMeasure, MeasureQueryDTO.class);
        dto.setRefCount(datamodelReferencService.measureReferenceCount(dssDatamodelMeasure.getName()));
        return dto;
    }


    @Override
    public int measureThemeReferenceCount(String name) {
        int count = getBaseMapper().selectCount(Wrappers.<DssDatamodelMeasure>lambdaQuery().eq(DssDatamodelMeasure::getWarehouseThemeName, name));
        int countEn = getBaseMapper().selectCount(Wrappers.<DssDatamodelMeasure>lambdaQuery().eq(DssDatamodelMeasure::getWarehouseThemeNameEn, name));
        return count + countEn;
    }
}
