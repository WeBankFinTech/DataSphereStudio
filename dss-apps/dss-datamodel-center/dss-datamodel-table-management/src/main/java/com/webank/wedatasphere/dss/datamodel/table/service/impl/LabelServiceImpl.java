package com.webank.wedatasphere.dss.datamodel.table.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.gson.Gson;
import com.webank.wedatasphere.dss.datamodel.center.common.constant.ErrorCode;
import com.webank.wedatasphere.dss.datamodel.center.common.context.DataModelSecurityContextHolder;
import com.webank.wedatasphere.dss.datamodel.center.common.event.CreateLabelEvent;
import com.webank.wedatasphere.dss.datamodel.center.common.event.DeleteLabelEvent;
import com.webank.wedatasphere.dss.datamodel.center.common.event.UpdateLabelEvent;
import com.webank.wedatasphere.dss.datamodel.center.common.exception.DSSDatamodelCenterException;
import com.webank.wedatasphere.dss.datamodel.center.common.service.AssertsSyncService;
import com.webank.wedatasphere.dss.datamodel.center.common.service.DatamodelReferencService;
import com.webank.wedatasphere.dss.datamodel.table.dao.DssDatamodelLabelMapper;
import com.webank.wedatasphere.dss.datamodel.table.dto.LabelQueryDTO;
import com.webank.wedatasphere.dss.datamodel.table.entity.DssDatamodelLabel;
import com.webank.wedatasphere.dss.datamodel.table.entity.DssDatamodelTable;
import com.webank.wedatasphere.dss.datamodel.table.service.LabelService;
import com.webank.wedatasphere.dss.datamodel.table.vo.LabelAddVO;
import com.webank.wedatasphere.dss.datamodel.table.vo.LabelEnableVO;
import com.webank.wedatasphere.dss.datamodel.table.vo.LabelUpdateVO;
import com.webank.wedatasphere.dss.datamodel.table.vo.LabelsQueryVO;
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
public class LabelServiceImpl extends ServiceImpl<DssDatamodelLabelMapper, DssDatamodelLabel> implements LabelService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LabelServiceImpl.class);

    private final Gson gson = new Gson();

    @Resource
    private DatamodelReferencService datamodelReferencService;

    @Resource
    private ModelMapper modelMapper;

    @Resource
    private AssertsSyncService assertsSyncService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long add(LabelAddVO vo) throws ErrorException {
        if (getBaseMapper().selectCount(Wrappers.<DssDatamodelLabel>lambdaQuery().eq(DssDatamodelLabel::getName, vo.getName())
                .or().eq(DssDatamodelLabel::getFieldIdentifier,vo.getFieldIdentifier())) > 0) {
            LOGGER.error("errorCode : {}, label name or field identifier can not repeat, name : {}", ErrorCode.LABEL_ADD_ERROR.getCode(), vo.getName());
            throw new DSSDatamodelCenterException(ErrorCode.LABEL_ADD_ERROR.getCode(), "label name or field identifier can not repeat");
        }

        if (datamodelReferencService.labelReferenceCount(vo.getName())>0
                ||datamodelReferencService.labelReferenceCount(vo.getFieldIdentifier())>0){
            LOGGER.error("errorCode : {}, label name can not be referenced", ErrorCode.DIMENSION_ADD_ERROR.getCode());
            throw new DSSDatamodelCenterException(ErrorCode.DIMENSION_ADD_ERROR.getCode(), "label name can not be referenced");
        }


        DssDatamodelLabel newOne = modelMapper.map(vo,DssDatamodelLabel.class);
        newOne.setParams(gson.toJson(vo.getParamMap()));
        newOne.setCreateTime(new Date());
        newOne.setUpdateTime(new Date());

        getBaseMapper().insert(newOne);

        //todo 同步资产创建标签
        assertsSyncService.syncCreateLabel(new CreateLabelEvent(this
                , DataModelSecurityContextHolder.getContext().getDataModelAuthentication().getUser()
                , vo.getName()));

        return newOne.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int enable(Long id, LabelEnableVO vo) throws ErrorException {
        DssDatamodelLabel enableOne = new DssDatamodelLabel();
        enableOne.setIsAvailable(vo.getIsAvailable());
        enableOne.setUpdateTime(new Date());
        return getBaseMapper().update(enableOne, Wrappers.<DssDatamodelLabel>lambdaUpdate().eq(DssDatamodelLabel::getId,id));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(Long id , LabelUpdateVO vo) throws ErrorException {
        DssDatamodelLabel ori = getBaseMapper().selectById(id);
        if (ori == null) {
            LOGGER.error("errorCode : {}, update label error not exists", ErrorCode.LABEL_UPDATE_ERROR.getCode());
            throw new DSSDatamodelCenterException(ErrorCode.LABEL_UPDATE_ERROR.getCode(), "update label error not exists");
        }

        if (datamodelReferencService.labelReferenceCount(ori.getName())>0
                ||datamodelReferencService.labelReferenceCount(ori.getFieldIdentifier())>0
                ||datamodelReferencService.labelReferenceCount(vo.getName())>0
                ||datamodelReferencService.labelReferenceCount(vo.getFieldIdentifier())>0){
            LOGGER.error("errorCode : {}, label name can not  referenced", ErrorCode.DIMENSION_UPDATE_ERROR.getCode());
            throw new DSSDatamodelCenterException(ErrorCode.DIMENSION_UPDATE_ERROR.getCode(), "label name can not  referenced");
        }

        //当更新名称时
        if (!StringUtils.equals(vo.getName(), ori.getName())) {
            int repeat = getBaseMapper().selectCount(Wrappers.<DssDatamodelLabel>lambdaQuery().eq(DssDatamodelLabel::getName, vo.getName()));
            if (repeat > 0) {
                LOGGER.error("errorCode : {}, label name can not repeat ", ErrorCode.DIMENSION_UPDATE_ERROR.getCode());
                throw new DSSDatamodelCenterException(ErrorCode.DIMENSION_UPDATE_ERROR.getCode(), "label name can not repeat ");
            }
        }

        String orgFieldIdentifier = ori.getFieldIdentifier();
        //当更新标识时
        if (!StringUtils.equals(vo.getFieldIdentifier(), orgFieldIdentifier)) {
            int repeat = getBaseMapper().selectCount(Wrappers.<DssDatamodelLabel>lambdaQuery().eq(DssDatamodelLabel::getFieldIdentifier, vo.getFieldIdentifier()));
            if (repeat > 0) {
                LOGGER.error("errorCode : {}, label field identifier can not repeat", ErrorCode.DIMENSION_UPDATE_ERROR.getCode());
                throw new DSSDatamodelCenterException(ErrorCode.DIMENSION_UPDATE_ERROR.getCode(), "label field identifier can not repeat ");
            }
        }

        DssDatamodelLabel updateOne = modelMapper.map(vo,DssDatamodelLabel.class);
        updateOne.setParams(gson.toJson(vo.getParamMap()));
        updateOne.setUpdateTime(new Date());
        getBaseMapper().update(updateOne, Wrappers.<DssDatamodelLabel>lambdaUpdate().eq(DssDatamodelLabel::getId,id));

        //todo 同步资产
        assertsSyncService.syncUpdateLabel(new UpdateLabelEvent(this
                , DataModelSecurityContextHolder.getContext().getDataModelAuthentication().getUser()
                , vo.getName(),ori.getName()));
        return 1;
    }

    @Override
    public LabelQueryDTO query(Long id) throws ErrorException {
        DssDatamodelLabel dssDatamodelLabel = getBaseMapper().selectById(id);
        if (dssDatamodelLabel == null){
            throw new DSSDatamodelCenterException(ErrorCode.LABEL_QUERY_ERROR.getCode(), "label id " +id +" not exists");
        }
        LabelQueryDTO dto = modelMapper.map(dssDatamodelLabel,LabelQueryDTO.class);
        dto.setRefCount(datamodelReferencService.labelReferenceCount(dssDatamodelLabel.getName()));
        return dto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delete(Long id) throws ErrorException {
        DssDatamodelLabel dssDatamodelLabel = getBaseMapper().selectById(id);
        if (dssDatamodelLabel == null){
            throw new DSSDatamodelCenterException(ErrorCode.LABEL_DELETE_ERROR.getCode(), "label id " +id +" not exists");
        }

        //校验引用情况
        if(datamodelReferencService.labelReferenceCount(dssDatamodelLabel.getName())>0||datamodelReferencService.labelReferenceCount(dssDatamodelLabel.getFieldIdentifier())>0){
            throw new DSSDatamodelCenterException(ErrorCode.LABEL_DELETE_ERROR.getCode(), "label id " +id +" has referenced");
        }

        getBaseMapper().deleteById(id);

        //todo 同步资产
        assertsSyncService.syncDeleteLabel(new DeleteLabelEvent(this
                , DataModelSecurityContextHolder.getContext().getDataModelAuthentication().getUser()
                , dssDatamodelLabel.getName()));
        return 1;
    }

    @Override
    public Message list(LabelsQueryVO vo) throws ErrorException {
        PageHelper.clearPage();
        PageHelper.startPage(vo.getPageNum(), vo.getPageSize());
        PageInfo<DssDatamodelLabel> pageInfo = new PageInfo<>(getBaseMapper().selectList(Wrappers.<DssDatamodelLabel>lambdaQuery()
                .eq(StringUtils.isNotBlank(vo.getWarehouseThemeName()), DssDatamodelLabel::getWarehouseThemeName, vo.getWarehouseThemeName())
                .eq(vo.getIsAvailable()!=null,DssDatamodelLabel::getIsAvailable,vo.getIsAvailable())
                .eq(StringUtils.isNotBlank(vo.getOwner()),DssDatamodelLabel::getOwner,vo.getOwner())
                .like(StringUtils.isNotBlank(vo.getName()), DssDatamodelLabel::getName, vo.getName())));
        return Message.ok()
                .data("list",pageInfo
                        .getList()
                        .stream()
                        .map(dssDatamodelLabel ->{
                            LabelQueryDTO dto = modelMapper.map(dssDatamodelLabel, LabelQueryDTO.class);
                            dto.setRefCount(datamodelReferencService.labelReferenceCount(dssDatamodelLabel.getName()));
                            return dto;
                        })
                        .collect(Collectors.toList()))
                .data("total",pageInfo.getTotal());
    }

    @Override
    public int labelThemeReferenceCount(String themeName) {
        int count = getBaseMapper().selectCount(Wrappers.<DssDatamodelLabel>lambdaQuery().eq(DssDatamodelLabel::getWarehouseThemeName,themeName));
        int countEn = getBaseMapper().selectCount(Wrappers.<DssDatamodelLabel>lambdaQuery().eq(DssDatamodelLabel::getWarehouseThemeNameEn,themeName));
        return count + countEn;
    }
}
