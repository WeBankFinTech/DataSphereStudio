package com.webank.wedatasphere.dss.datamodel.dimension.service.impl;

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
import com.webank.wedatasphere.dss.datamodel.center.common.service.DimensionIndicatorCheckService;
import com.webank.wedatasphere.dss.datamodel.center.common.service.DimensionTableCheckService;
import com.webank.wedatasphere.dss.datamodel.dimension.dao.DssDatamodelDimensionMapper;
import com.webank.wedatasphere.dss.datamodel.dimension.dto.DimensionQueryDTO;
import com.webank.wedatasphere.dss.datamodel.dimension.entity.DssDatamodelDimension;
import com.webank.wedatasphere.dss.datamodel.dimension.service.DimensionService;
import com.webank.wedatasphere.dss.datamodel.dimension.vo.DimensionAddVO;
import com.webank.wedatasphere.dss.datamodel.dimension.vo.DimensionEnableVO;
import com.webank.wedatasphere.dss.datamodel.dimension.vo.DimensionQueryVO;
import com.webank.wedatasphere.dss.datamodel.dimension.vo.DimensionUpdateVO;
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
public class DimensionServiceImpl extends ServiceImpl<DssDatamodelDimensionMapper,DssDatamodelDimension>  implements DimensionService {


    private static final Logger LOGGER = LoggerFactory.getLogger(DimensionServiceImpl.class);

    private final ModelMapper modelMapper = new ModelMapper();

    @Resource
    private DimensionIndicatorCheckService dimensionIndicatorCheckService;

    @Resource
    private DimensionTableCheckService dimensionTableCheckService;

    @Resource
    private AssertsSyncService assertsSyncService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long addDimension(DimensionAddVO vo) throws DSSDatamodelCenterException{
        if (getBaseMapper().selectCount(Wrappers.<DssDatamodelDimension>lambdaQuery().eq(DssDatamodelDimension::getName, vo.getName())
                .or().eq(DssDatamodelDimension::getFieldIdentifier,vo.getFieldIdentifier())) > 0) {
            LOGGER.error("errorCode : {}, dimension name or field identifier can not repeat, name : {}", ErrorCode.INDICATOR_ADD_ERROR.getCode(), vo.getName());
            throw new DSSDatamodelCenterException(ErrorCode.DIMENSION_ADD_ERROR.getCode(), "dimension name or field identifier can not repeat");
        }

        DssDatamodelDimension newOne = modelMapper.map(vo,DssDatamodelDimension.class);
        newOne.setCreateTime(new Date());
        newOne.setUpdateTime(new Date());

        getBaseMapper().insert(newOne);

        //同步atlas
        CreateModelTypeResult result = assertsSyncService.syncCreateModel(
                new CreateModelEvent(this
                        , DataModelSecurityContextHolder.getContext().getDataModelAuthentication().getUser()
                        , vo.getFieldIdentifier()
                        , ClassificationConstant.DIMENSION));
        return newOne.getId();
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public int enableDimension(Long id ,DimensionEnableVO vo) {
        DssDatamodelDimension enableOne = new DssDatamodelDimension();
        enableOne.setIsAvailable(vo.getIsAvailable());
        enableOne.setUpdateTime(new Date());
        return getBaseMapper().update(enableOne, Wrappers.<DssDatamodelDimension>lambdaUpdate().eq(DssDatamodelDimension::getId,id));
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateDimension(Long id, DimensionUpdateVO vo) throws DSSDatamodelCenterException{
        DssDatamodelDimension org = getBaseMapper().selectById(id);
        if (org == null) {
            LOGGER.error("errorCode : {}, update dimension error not exists", ErrorCode.DIMENSION_UPDATE_ERROR.getCode());
            throw new DSSDatamodelCenterException(ErrorCode.DIMENSION_UPDATE_ERROR.getCode(), "update dimension error not exists");
        }


        //当更新名称时
        if (!StringUtils.equals(vo.getName(), org.getName())) {
            int repeat = getBaseMapper().selectCount(Wrappers.<DssDatamodelDimension>lambdaQuery().eq(DssDatamodelDimension::getName, vo.getName()));

            if (repeat > 0 ||(dimensionIndicatorCheckService.referenceCase(org.getName()))||dimensionTableCheckService.referenceCase(org.getName())) {
                LOGGER.error("errorCode : {}, dimension name can not repeat or referenced", ErrorCode.DIMENSION_UPDATE_ERROR.getCode());
                throw new DSSDatamodelCenterException(ErrorCode.DIMENSION_UPDATE_ERROR.getCode(), "dimension name can not repeat or referenced");
            }
        }

        String orgFieldIdentifier = org.getFieldIdentifier();
        //当更新标识时
        if (!StringUtils.equals(vo.getFieldIdentifier(), orgFieldIdentifier)) {
            int repeat = getBaseMapper().selectCount(Wrappers.<DssDatamodelDimension>lambdaQuery().eq(DssDatamodelDimension::getFieldIdentifier, vo.getFieldIdentifier()));

            if (repeat > 0 ||(dimensionIndicatorCheckService.referenceEn(orgFieldIdentifier))||dimensionIndicatorCheckService.referenceEn(orgFieldIdentifier)) {
                LOGGER.error("errorCode : {}, dimension field identifier can not repeat or referenced or referenced", ErrorCode.DIMENSION_UPDATE_ERROR.getCode());
                throw new DSSDatamodelCenterException(ErrorCode.DIMENSION_UPDATE_ERROR.getCode(), "dimension field identifier can not repeat or referenced");
            }
        }
        DssDatamodelDimension updateOne = modelMapper.map(vo,DssDatamodelDimension.class);
        updateOne.setUpdateTime(new Date());
        getBaseMapper().update(updateOne, Wrappers.<DssDatamodelDimension>lambdaUpdate().eq(DssDatamodelDimension::getId,id));

        //同步atlas
        UpdateModelTypeResult updateModelTypeResult = assertsSyncService.syncUpdateModel(
                new UpdateModelEvent(this
                        ,DataModelSecurityContextHolder.getContext().getDataModelAuthentication().getUser()
                        ,vo.getFieldIdentifier()
                        ,orgFieldIdentifier
                        ,ClassificationConstant.DIMENSION));
        return 1;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteDimension(Long id) throws ErrorException{

        DssDatamodelDimension dssDatamodelDimension = getBaseMapper().selectById(id);
        if (dssDatamodelDimension == null){
            throw new DSSDatamodelCenterException(ErrorCode.DIMENSION_DELETE_ERROR.getCode(), "dimension id " +id +" not exists");
        }
        //校验引用情况
        if (dimensionIndicatorCheckService.referenceCase(dssDatamodelDimension.getName())||dimensionTableCheckService.referenceCaseEn(dssDatamodelDimension.getFieldIdentifier())
        ||dimensionTableCheckService.referenceCase(dssDatamodelDimension.getName())||dimensionTableCheckService.referenceCaseEn(dssDatamodelDimension.getFieldIdentifier())){
            throw new DSSDatamodelCenterException(ErrorCode.DIMENSION_DELETE_ERROR.getCode(), "dimension id " +id +" has referenced");
        }
        getBaseMapper().deleteById(id);
        //同步资产
        assertsSyncService.syncDeleteModel(new DeleteModelEvent(this
                ,DataModelSecurityContextHolder.getContext().getDataModelAuthentication().getUser()
                ,dssDatamodelDimension.getFieldIdentifier()
                ,ClassificationConstant.DIMENSION));
        return 1;
    }


    @Override
    public Message listDimensions(DimensionQueryVO vo) {
        QueryWrapper<DssDatamodelDimension> queryWrapper = new QueryWrapper<DssDatamodelDimension>()
                .like(StringUtils.isNotBlank(vo.getName()),"name",vo.getName())
                .eq(vo.getIsAvailable()!=null,"is_available",vo.getIsAvailable())
                .eq(StringUtils.isNotBlank(vo.getWarehouseThemeName()),"warehouse_theme_name",vo.getWarehouseThemeName())
                .like(StringUtils.isNotBlank(vo.getOwner()),"owner",vo.getOwner());
        PageHelper.clearPage();
        PageHelper.startPage(vo.getPageNum(),vo.getPageSize());
        PageInfo<DssDatamodelDimension> pageInfo = new PageInfo<>(getBaseMapper().selectList(queryWrapper));
        //IPage<DssDatamodelDimension> iPage = page(new Page<>(vo.getPageNum(),vo.getPageSize()),queryWrapper);

        return Message.ok()
                .data("list",pageInfo
                        .getList()
                        .stream()
                        .map(dssDatamodelDimension -> modelMapper.map(dssDatamodelDimension, DimensionQueryDTO.class))
                        .collect(Collectors.toList()))
                .data("total",pageInfo.getTotal());
    }


    @Override
    public DimensionQueryDTO queryById(Long id) throws ErrorException {
        DssDatamodelDimension dssDatamodelDimension = getBaseMapper().selectById(id);
        if (dssDatamodelDimension == null){
            throw new DSSDatamodelCenterException(ErrorCode.DIMENSION_QUERY_ERROR.getCode(), "dimension id " +id +" not exists");
        }
        return modelMapper.map(dssDatamodelDimension,DimensionQueryDTO.class);
    }


    @Override
    public int dimensionThemeReferenceCount(String name) {
        int count = getBaseMapper().selectCount(Wrappers.<DssDatamodelDimension>lambdaQuery().eq(DssDatamodelDimension::getWarehouseThemeName,name));
        int countEn = getBaseMapper().selectCount(Wrappers.<DssDatamodelDimension>lambdaQuery().eq(DssDatamodelDimension::getWarehouseThemeName,name));
        return count + countEn;
    }
}
