package com.webank.wedatasphere.dss.datamodel.indicator.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.webank.wedatasphere.dss.data.governance.entity.ClassificationConstant;
import com.webank.wedatasphere.dss.data.governance.impl.LinkisDataAssetsRemoteClient;
import com.webank.wedatasphere.dss.data.governance.response.CreateModelTypeResult;
import com.webank.wedatasphere.dss.data.governance.response.UpdateModelTypeResult;
import com.webank.wedatasphere.dss.datamodel.center.common.constant.ErrorCode;
import com.webank.wedatasphere.dss.datamodel.center.common.constant.IndicatorSourceInfoConstant;
import com.webank.wedatasphere.dss.datamodel.center.common.context.DataModelSecurityContextHolder;
import com.webank.wedatasphere.dss.datamodel.center.common.event.CreateModelEvent;
import com.webank.wedatasphere.dss.datamodel.center.common.event.DeleteModelEvent;
import com.webank.wedatasphere.dss.datamodel.center.common.event.UpdateModelEvent;
import com.webank.wedatasphere.dss.datamodel.center.common.exception.DSSDatamodelCenterException;
import com.webank.wedatasphere.dss.datamodel.center.common.service.AssertsSyncService;
import com.webank.wedatasphere.dss.datamodel.center.common.service.DatamodelReferencService;
import com.webank.wedatasphere.dss.datamodel.indicator.dao.DssDatamodelIndicatorMapper;
import com.webank.wedatasphere.dss.datamodel.indicator.dao.IndicatorQueryMapper;
import com.webank.wedatasphere.dss.datamodel.indicator.dto.IndicatorContentQueryDTO;
import com.webank.wedatasphere.dss.datamodel.indicator.dto.IndicatorQueryDTO;
import com.webank.wedatasphere.dss.datamodel.indicator.dto.IndicatorVersionDTO;
import com.webank.wedatasphere.dss.datamodel.indicator.dto.IndicatorVersionQueryDTO;
import com.webank.wedatasphere.dss.datamodel.indicator.entity.DssDatamodelIndicator;
import com.webank.wedatasphere.dss.datamodel.indicator.entity.DssDatamodelIndicatorContent;
import com.webank.wedatasphere.dss.datamodel.indicator.entity.DssDatamodelIndicatorQuery;
import com.webank.wedatasphere.dss.datamodel.indicator.entity.DssDatamodelIndicatorVersion;
import com.webank.wedatasphere.dss.datamodel.indicator.service.IndicatorContentService;
import com.webank.wedatasphere.dss.datamodel.indicator.service.IndicatorService;
import com.webank.wedatasphere.dss.datamodel.indicator.service.IndicatorVersionService;
import com.webank.wedatasphere.dss.datamodel.indicator.vo.*;
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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class IndicatorServiceImpl extends ServiceImpl<DssDatamodelIndicatorMapper, DssDatamodelIndicator> implements IndicatorService {

    private static final Logger LOGGER = LoggerFactory.getLogger(IndicatorServiceImpl.class);


    private final Gson gson = new Gson();

    @Resource
    private ModelMapper modelMapper;

    @Resource
    private IndicatorContentService indicatorContentService;

    @Resource
    private IndicatorVersionService indicatorVersionService;

    @Resource
    private IndicatorQueryMapper indicatorQueryMapper;


    @Resource
    private LinkisDataAssetsRemoteClient linkisDataAssetsRemoteClient;

    @Resource
    private AssertsSyncService assertsSyncService;


    @Resource
    private DatamodelReferencService datamodelReferencService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addIndicator(IndicatorAddVO vo, String version) throws ErrorException {
        if (getBaseMapper().selectCount(Wrappers.<DssDatamodelIndicator>lambdaQuery().eq(DssDatamodelIndicator::getName, vo.getName())
                .or().eq(DssDatamodelIndicator::getFieldIdentifier, vo.getFieldIdentifier())) > 0) {
            LOGGER.error("errorCode : {}, indicator name or field identifier can not repeat, name : {}", ErrorCode.INDICATOR_ADD_ERROR.getCode(), vo.getName());
            throw new DSSDatamodelCenterException(ErrorCode.INDICATOR_ADD_ERROR.getCode(), "indicator name or field identifier can not repeat");
        }
        //校验引用情况
        if (datamodelReferencService.indicatorReferenceCount(vo.getName())>0
                ||datamodelReferencService.indicatorReferenceCount(vo.getFieldIdentifier())>0){
            LOGGER.error("errorCode : {}, indicator name can not  referenced", ErrorCode.INDICATOR_ADD_ERROR.getCode());
            throw new DSSDatamodelCenterException(ErrorCode.INDICATOR_ADD_ERROR.getCode(), "indicator name can not referenced");
        }


        DssDatamodelIndicator newOne = modelMapper.map(vo, DssDatamodelIndicator.class);
        //基本信息中设置版本号
        newOne.setVersion(version);
        newOne.setCreateTime(new Date());
        newOne.setUpdateTime(new Date());
        if (!save(newOne)) {
            LOGGER.error("errorCode : {}, add indicator error", ErrorCode.INDICATOR_ADD_ERROR.getCode());
            throw new DSSDatamodelCenterException(ErrorCode.INDICATOR_ADD_ERROR.getCode(), "add indicator error");
        }

        indicatorContentService.addIndicatorContent(newOne.getId(), version, vo.getContent());

        //同步atlas
        CreateModelTypeResult result = assertsSyncService.syncCreateModel(
                new CreateModelEvent(this
                        , DataModelSecurityContextHolder.getContext().getDataModelAuthentication().getUser()
                        , vo.getFieldIdentifier()
                        , ClassificationConstant.INDICATOR));
        return newOne.getId();
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateIndicator(Long id, IndicatorUpdateVO vo) throws ErrorException {
        DssDatamodelIndicator org = getBaseMapper().selectById(id);
        if (org == null) {
            LOGGER.error("errorCode : {}, update indicator error not exists", ErrorCode.INDICATOR_UPDATE_ERROR.getCode());
            throw new DSSDatamodelCenterException(ErrorCode.INDICATOR_UPDATE_ERROR.getCode(), "update indicator error not exists");
        }

        //校验引用情况
        if (datamodelReferencService.indicatorReferenceCount(org.getName())>0
                ||datamodelReferencService.indicatorReferenceCount(vo.getName())>0
                ||datamodelReferencService.indicatorReferenceCount(org.getFieldIdentifier())>0
                ||datamodelReferencService.indicatorReferenceCount(vo.getFieldIdentifier())>0){
            LOGGER.error("errorCode : {}, indicator name can not  referenced", ErrorCode.INDICATOR_UPDATE_ERROR.getCode());
            throw new DSSDatamodelCenterException(ErrorCode.INDICATOR_UPDATE_ERROR.getCode(), "indicator name can not referenced");
        }


        DssDatamodelIndicatorContent orgContent = indicatorContentService.queryByIndicateId(id);


        //当更新指标名称时,存在其他指标名称同名或者当前指标名称已经存在版本信息，则不允许修改指标名称
        if (!StringUtils.equals(vo.getName(), org.getName())) {
            int repeat = getBaseMapper().selectCount(Wrappers.<DssDatamodelIndicator>lambdaQuery().eq(DssDatamodelIndicator::getName, vo.getName()));
            String lastVersion = indicatorVersionService.findLastVersion(org.getName());
            if (repeat > 0 || StringUtils.isNotBlank(lastVersion)) {
                LOGGER.error("errorCode : {}, indicator name can not repeat or has version", ErrorCode.INDICATOR_UPDATE_ERROR.getCode());
                throw new DSSDatamodelCenterException(ErrorCode.INDICATOR_UPDATE_ERROR.getCode(), "indicator name can not repeat or has version");
            }
        }

        String orgFieldIdentifier =  org.getFieldIdentifier();
        //当更新指标标识时,存在其他指标名称同名或者当前指标名称已经存在版本信息，则不允许修改指标名称
        if (!StringUtils.equals(vo.getFieldIdentifier(), orgFieldIdentifier)) {
            int repeat = getBaseMapper().selectCount(Wrappers.<DssDatamodelIndicator>lambdaQuery().eq(DssDatamodelIndicator::getFieldIdentifier, vo.getFieldIdentifier()));
            if (repeat > 0) {
                LOGGER.error("errorCode : {}, indicator field identifier can not repeat or has version", ErrorCode.INDICATOR_UPDATE_ERROR.getCode());
                throw new DSSDatamodelCenterException(ErrorCode.INDICATOR_UPDATE_ERROR.getCode(), "indicator field identifier can not repeat or has version");
            }
        }


        Long orgId = org.getId();
        String version = org.getVersion();

        //更新原有指标
        DssDatamodelIndicator updateOne = modelMapper.map(vo, DssDatamodelIndicator.class);
        updateOne.setUpdateTime(new Date());
        int result = getBaseMapper().update(updateOne, Wrappers.<DssDatamodelIndicator>lambdaUpdate().eq(DssDatamodelIndicator::getId, id));
        if (result != 1) {
            LOGGER.error("errorCode : {}, update indicator error not exists", ErrorCode.INDICATOR_UPDATE_ERROR.getCode());
            throw new DSSDatamodelCenterException(ErrorCode.INDICATOR_UPDATE_ERROR.getCode(), "update indicator error not exists");
        }

        indicatorContentService.updateIndicatorContent(orgId, version, vo.getContent());



        //同步atlas
        UpdateModelTypeResult updateModelTypeResult = assertsSyncService.syncUpdateModel(
                new UpdateModelEvent(this
                        ,DataModelSecurityContextHolder.getContext().getDataModelAuthentication().getUser()
                        ,vo.getFieldIdentifier()
                        ,orgFieldIdentifier
                        ,ClassificationConstant.INDICATOR)
        );


        return 1;
    }


    @Override
    public int deleteIndicator(Long id) throws ErrorException {
        DssDatamodelIndicator dssDatamodelIndicator = getBaseMapper().selectById(id);
        if (dssDatamodelIndicator == null) {
            throw new DSSDatamodelCenterException(ErrorCode.INDICATOR_DELETE_ERROR.getCode(), "indicator id " + id + " not exists");
        }

        //校验引用情况
        if (datamodelReferencService.indicatorReferenceCount(dssDatamodelIndicator.getName())>0
                ||datamodelReferencService.indicatorReferenceCount(dssDatamodelIndicator.getFieldIdentifier())>0){
            throw new DSSDatamodelCenterException(ErrorCode.INDICATOR_DELETE_ERROR.getCode(), "indicator id " + id + " has referenced");
        }


//        //有版本不能删除
//        if (indicatorVersionService.getBaseMapper().selectCount(Wrappers.<DssDatamodelIndicatorVersion>lambdaQuery().eq(DssDatamodelIndicatorVersion::getName, dssDatamodelIndicator.getName())) > 0) {
//            throw new DSSDatamodelCenterException(ErrorCode.INDICATOR_DELETE_ERROR.getCode(), "indicator id " + id + " has version");
//        }
        //同时删除版本
        indicatorVersionService.getBaseMapper()
                .delete(Wrappers.<DssDatamodelIndicatorVersion>lambdaQuery().eq(DssDatamodelIndicatorVersion::getName, dssDatamodelIndicator.getName()));
        //删除指标
        getBaseMapper().deleteById(id);
        indicatorContentService.getBaseMapper().delete(Wrappers.<DssDatamodelIndicatorContent>lambdaQuery().eq(DssDatamodelIndicatorContent::getIndicatorId, id));


        //同步资产
        assertsSyncService.syncDeleteModel(new DeleteModelEvent(this
                ,DataModelSecurityContextHolder.getContext().getDataModelAuthentication().getUser()
                ,dssDatamodelIndicator.getFieldIdentifier()
                ,ClassificationConstant.INDICATOR));

        return 1;
    }


    @Override
    public Message listIndicators(IndicatorQueryVO vo) {
        QueryWrapper<DssDatamodelIndicatorQuery> queryWrapper = new QueryWrapper<DssDatamodelIndicatorQuery>()
                .like(StringUtils.isNotBlank(vo.getName()), "name", vo.getName())
                .eq(vo.getIsAvailable() != null, "is_available", vo.getIsAvailable())
                .eq(vo.getIndicatorType() != null, "indicator_type", vo.getIndicatorType())
                .eq(StringUtils.isNotBlank(vo.getWarehouseThemeName()), "theme_area", vo.getWarehouseThemeName())
                .like(StringUtils.isNotBlank(vo.getOwner()), "owner", vo.getOwner());
        PageHelper.clearPage();
        PageHelper.startPage(vo.getPageNum(), vo.getPageSize());
        PageInfo<DssDatamodelIndicatorQuery> pageInfo = new PageInfo<>(indicatorQueryMapper.page(queryWrapper));
        //IPage<DssDatamodelIndicatorQuery> iPage = indicatorQueryMapper.page(new Page<>(vo.getPageNum(), vo.getPageSize()), queryWrapper);

        return Message.ok()
                .data("list", pageInfo.getList().stream().peek(e-> e.setRefCount(datamodelReferencService.indicatorReferenceCount(e.getName()))).collect(Collectors.toList()))
                .data("total", pageInfo.getTotal());
    }


    @Override
    public Message queryById(Long id) throws DSSDatamodelCenterException {
        DssDatamodelIndicator indicator = baseMapper.selectById(id);
        if (indicator == null) {
            LOGGER.error("errorCode : {}, indicator id : {} not exists", ErrorCode.INDICATOR_QUERY_ERROR.getCode(), id);
            throw new DSSDatamodelCenterException(ErrorCode.INDICATOR_QUERY_ERROR.getCode(), "indicator id " + id + " not exists");
        }

        DssDatamodelIndicatorContent content = indicatorContentService.queryByIndicateId(id);
        if (content == null) {
            LOGGER.error("errorCode : {}, indicator content indicatorId : {} not exists", ErrorCode.INDICATOR_QUERY_ERROR.getCode(), id);
            throw new DSSDatamodelCenterException(ErrorCode.INDICATOR_QUERY_ERROR.getCode(), "indicator content id " + id + " not exists");
        }

        IndicatorQueryDTO indicatorQueryDTO = modelMapper.map(indicator, IndicatorQueryDTO.class);
        IndicatorContentQueryDTO indicatorContentQueryDTO = modelMapper.map(content, IndicatorContentQueryDTO.class);
        indicatorQueryDTO.setContent(indicatorContentQueryDTO);
        indicatorQueryDTO.setRefCount(datamodelReferencService.indicatorReferenceCount(indicator.getName()));
        return Message.ok().data("detail", indicatorQueryDTO);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public int enableIndicator(Long id, IndicatorEnableVO vo) {
        DssDatamodelIndicator updateOne = modelMapper.map(vo, DssDatamodelIndicator.class);
        updateOne.setUpdateTime(new Date());
        return getBaseMapper().update(updateOne, Wrappers.<DssDatamodelIndicator>lambdaUpdate().eq(DssDatamodelIndicator::getId, id));
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public int addIndicatorVersion(Long id, IndicatorVersionAddVO vo) throws ErrorException {
        //todo 生成新版本 则旧版本是否判断存在 关联其他表信息

        //判断旧版本指标是否存在
        DssDatamodelIndicator orgVersion = getBaseMapper().selectById(id);
        if (orgVersion == null) {
            LOGGER.error("errorCode : {}, indicator id : {} not exists", ErrorCode.INDICATOR_VERSION_ADD_ERROR.getCode(), id);
            throw new DSSDatamodelCenterException(ErrorCode.INDICATOR_VERSION_ADD_ERROR.getCode(), "indicator  id " + id + " not exists");
        }

        //校验引用情况
        if (datamodelReferencService.indicatorReferenceCount(orgVersion.getName())>0
                ||datamodelReferencService.indicatorReferenceCount(vo.getName())>0
                ||datamodelReferencService.indicatorReferenceCount(orgVersion.getFieldIdentifier())>0
                ||datamodelReferencService.indicatorReferenceCount(vo.getFieldIdentifier())>0){
            LOGGER.error("errorCode : {}, indicator name can not  referenced", ErrorCode.INDICATOR_VERSION_ADD_ERROR.getCode());
            throw new DSSDatamodelCenterException(ErrorCode.INDICATOR_VERSION_ADD_ERROR.getCode(), "indicator name can not referenced");
        }




        String orgName = orgVersion.getName();
        String orgNameEn = orgVersion.getFieldIdentifier();
        String orgV = orgVersion.getVersion();
        String assignVersion = newVersion(orgName, orgV);

        //指标名称不能改变
        if (!StringUtils.equals(orgName, vo.getName()) || !StringUtils.equals(orgNameEn, vo.getFieldIdentifier())) {
            LOGGER.error("errorCode : {}, indicator name or fieldIdentifier must be same", ErrorCode.INDICATOR_VERSION_ADD_ERROR.getCode());
            throw new DSSDatamodelCenterException(ErrorCode.INDICATOR_VERSION_ADD_ERROR.getCode(), "indicator name or fieldIdentifier must be same");
        }

        //查询指标详细信息
        DssDatamodelIndicatorContent orgContent = indicatorContentService.queryByIndicateId(id);
        if (orgContent == null) {
            LOGGER.error("errorCode : {}, indicator content indicatorId : {} not exists", ErrorCode.INDICATOR_VERSION_ADD_ERROR.getCode(), id);
            throw new DSSDatamodelCenterException(ErrorCode.INDICATOR_VERSION_ADD_ERROR.getCode(), "indicator content id " + id + " not exists");
        }

        //旧版本备份
        indicatorVersionService.addOlderVersion(orgName, orgVersion.getOwner(), orgVersion.getPrincipalName(), orgV, orgVersion.getComment(), IndicatorVersionDTO.of(orgVersion, orgContent));


        //删除旧版本
        getBaseMapper().deleteById(id);
        indicatorContentService.deleteByIndicatorId(id);

        //填充新增版本
        DssDatamodelIndicator newOne = modelMapper.map(vo, DssDatamodelIndicator.class);
        //基本信息中设置版本号,如果新增时未指定版本号则生成新版本号
        String newVersion = assignVersion;
        newOne.setVersion(newVersion);
        newOne.setCreateTime(new Date());
        newOne.setUpdateTime(new Date());
        //保存新版本
        if (!save(newOne)) {
            throw new DSSDatamodelCenterException(ErrorCode.INDICATOR_VERSION_ADD_ERROR.getCode(), "save new version indicator error");

        }
        //更新指标详细信息
        indicatorContentService.updateIndicatorContent(newOne.getId(), newVersion, vo.getContent());

        return 1;
    }


    private String newVersion(String orgName, String orgVersion) {
        String lastVersion = indicatorVersionService.findLastVersion(orgName);

        return StringUtils.isBlank(lastVersion) ?
                (Integer.parseInt(orgVersion) + 1 + "") :
                (Integer.parseInt(lastVersion) > Integer.parseInt(orgVersion)) ? (Integer.parseInt(lastVersion) + 1 + "") :
                        (Integer.parseInt(orgVersion) + 1 + "");
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public int versionRollBack(IndicatorVersionRollBackVO vo) throws ErrorException {

        String name = vo.getName();
        String version = vo.getVersion();

        //获取当前使用版本指标
        DssDatamodelIndicator current =
                getBaseMapper().selectOne(Wrappers.<DssDatamodelIndicator>lambdaQuery().eq(DssDatamodelIndicator::getName, name));
        //判断当前使用指标
        if (current == null) {
            LOGGER.error("errorCode : {}, current indicator not exists, name : {} version : {}", ErrorCode.INDICATOR_VERSION_ROLL_BACK_ERROR.getCode(), name, version);
            throw new DSSDatamodelCenterException(ErrorCode.INDICATOR_VERSION_ROLL_BACK_ERROR.getCode(), "current indicator not exists, name : " + name + " version : " + version);
        }

        //校验引用情况
        if (datamodelReferencService.indicatorReferenceCount(current.getName())>0
                ||datamodelReferencService.indicatorReferenceCount(current.getFieldIdentifier())>0){
            throw new DSSDatamodelCenterException(ErrorCode.INDICATOR_VERSION_ROLL_BACK_ERROR.getCode(), "indicator id " + current.getId() + " has referenced");
        }


        //查询当前详细信息
        DssDatamodelIndicatorContent currentContent = indicatorContentService.queryByIndicateId(current.getId());
        if (currentContent == null) {
            LOGGER.error("errorCode : {}, current indicator content not exists, name : {} version : {}", ErrorCode.INDICATOR_VERSION_ROLL_BACK_ERROR.getCode(), name, version);
            throw new DSSDatamodelCenterException(ErrorCode.INDICATOR_VERSION_ROLL_BACK_ERROR.getCode(), "current indicator content not exists, name : " + name + " version : " + version);
        }
        //当前版本备份
        indicatorVersionService.addOlderVersion(current.getName(), current.getOwner(), current.getPrincipalName(), current.getVersion(), current.getComment(), IndicatorVersionDTO.of(current, currentContent));
        //删除当前版本
        getBaseMapper().deleteById(current.getId());
        //删除当前版本详情
        indicatorContentService.deleteByIndicatorId(current.getId());

        //获取需要回退指标版本信息
        DssDatamodelIndicatorVersion rollBackVersion = indicatorVersionService.findBackup(name, version);
        if (rollBackVersion == null) {
            LOGGER.error("errorCode : {}, indicator name : {} version : {}", ErrorCode.INDICATOR_VERSION_ROLL_BACK_ERROR.getCode(), name, version);
            throw new DSSDatamodelCenterException(ErrorCode.INDICATOR_VERSION_ROLL_BACK_ERROR.getCode(), "indicator name : " + name + " version : " + version + " not exist");
        }
        Long rollBackId = rollBackVersion.getId();

        IndicatorVersionDTO rollBackDTO = gson.fromJson(rollBackVersion.getVersionContext(), IndicatorVersionDTO.class);
        DssDatamodelIndicator rollBackIndicator = rollBackDTO.getEssential();
        //重置id
        rollBackIndicator.setId(null);
        save(rollBackIndicator);


        DssDatamodelIndicatorContent rollBackContent = rollBackDTO.getContent();
        //重置id
        rollBackContent.setId(null);
        //设置新指标id
        rollBackContent.setIndicatorId(rollBackIndicator.getId());
        indicatorContentService.save(rollBackContent);
        indicatorVersionService.getBaseMapper().deleteById(rollBackId);
        return 1;
    }


    @Override
    public Message listIndicatorVersions(IndicatorVersionQueryVO vo) {
        List<IndicatorVersionQueryDTO> list = indicatorVersionService.getBaseMapper()
                .selectList(Wrappers.<DssDatamodelIndicatorVersion>lambdaQuery().eq(DssDatamodelIndicatorVersion::getName, vo.getName()))
                .stream()
                .map(dssDatamodelIndicatorVersion -> modelMapper.map(dssDatamodelIndicatorVersion, IndicatorVersionQueryDTO.class))
                .collect(Collectors.toList());
        return Message.ok().data("list", list);
    }


    @Override
    public int indicatorThemeReferenceCount(String name) {
        int currentCount = getBaseMapper().selectCount(Wrappers.<DssDatamodelIndicator>lambdaQuery().eq(DssDatamodelIndicator::getThemeArea, name));
        int currentCountEn = getBaseMapper().selectCount(Wrappers.<DssDatamodelIndicator>lambdaQuery().eq(DssDatamodelIndicator::getThemeAreaEn, name));
        List<DssDatamodelIndicatorVersion> preReferences = indicatorVersionService.contentReferenceCount(name);
        int versionCount = (int) preReferences.stream().filter(e->{
            IndicatorVersionDTO rollBackDTO = gson.fromJson(e.getVersionContext(), IndicatorVersionDTO.class);
            DssDatamodelIndicator rollBackIndicator = rollBackDTO.getEssential();
            return StringUtils.equals(rollBackIndicator.getThemeArea(),name)||StringUtils.equals(rollBackIndicator.getThemeAreaEn(),name);
        }).count();
        return currentCount + versionCount + currentCountEn;
    }

    @Override
    public int indicatorLayerReferenceCount(String name) {
        int currentCount = getBaseMapper().selectCount(Wrappers.<DssDatamodelIndicator>lambdaQuery().eq(DssDatamodelIndicator::getLayerArea, name));
        int currentCountEn = getBaseMapper().selectCount(Wrappers.<DssDatamodelIndicator>lambdaQuery().eq(DssDatamodelIndicator::getLayerAreaEn, name));
        List<DssDatamodelIndicatorVersion> preReferences = indicatorVersionService.contentReferenceCount(name);
        int versionCount = (int) preReferences.stream().filter(e->{
            IndicatorVersionDTO rollBackDTO = gson.fromJson(e.getVersionContext(), IndicatorVersionDTO.class);
            DssDatamodelIndicator rollBackIndicator = rollBackDTO.getEssential();
            return StringUtils.equals(rollBackIndicator.getLayerArea(),name)||StringUtils.equals(rollBackIndicator.getLayerAreaEn(),name);
        }).count();
        return currentCount + versionCount + currentCountEn;
    }

    @Override
    public int indicatorCycleReferenceCount(String name) {
        List<DssDatamodelIndicatorContent> currentContents = indicatorContentService.sourceInfoReference(name);
        int currentCount = currentCountFromIndicatorContentSourceInfo(IndicatorSourceInfoConstant.cycle,name,currentContents);
        int currentCountEn = currentCountFromIndicatorContentSourceInfo(IndicatorSourceInfoConstant.cycleEn,name,currentContents);
        List<DssDatamodelIndicatorVersion> preReferences = indicatorVersionService.contentReferenceCount(name);
        int versionCount = versionCountFromIndicatorContentSourceInfo(preReferences,IndicatorSourceInfoConstant.cycle.name(),name);
        int versionCountEn = versionCountFromIndicatorContentSourceInfo(preReferences,IndicatorSourceInfoConstant.cycleEn.name(),name);
        return currentCount + currentCountEn + versionCount + versionCountEn;
    }

    private int versionCountFromIndicatorContentSourceInfo(List<DssDatamodelIndicatorVersion> preReferences,String type,String name){
        return (int) preReferences.stream().filter(e->{
            IndicatorVersionDTO rollBackDTO = gson.fromJson(e.getVersionContext(), IndicatorVersionDTO.class);
            DssDatamodelIndicatorContent rollBackContent = rollBackDTO.getContent();
            return filterFromIndicatorContentSourceInfo(type, name, rollBackContent);
        }).count();
    }

    private int currentCountFromIndicatorContentSourceInfo(IndicatorSourceInfoConstant type, String name,List<DssDatamodelIndicatorContent> contents){
        return (int) contents.stream().filter(e-> filterFromIndicatorContentSourceInfo(type.name(),name,e)).count();
    }

    private boolean filterFromIndicatorContentSourceInfo(String type, String name, DssDatamodelIndicatorContent rollBackContent) {
        Map<String,String> content =  gson.fromJson(rollBackContent.getIndicatorSourceInfo(),Map.class);
        if (!content.containsKey(type)){
            return false;
        }
        return Sets.newHashSet(StringUtils.split(content.get(type), ",")).contains(name);
    }

    @Override
    public int indicatorModifierReferenceCount(String name) {
        List<DssDatamodelIndicatorContent> currentContents = indicatorContentService.sourceInfoReference(name);
        int currentCount = currentCountFromIndicatorContentSourceInfo(IndicatorSourceInfoConstant.modifier,name,currentContents);
        int currentCountEn = currentCountFromIndicatorContentSourceInfo(IndicatorSourceInfoConstant.modifierEn,name,currentContents);
        List<DssDatamodelIndicatorVersion> preReferences = indicatorVersionService.contentReferenceCount(name);
        int versionCount = versionCountFromIndicatorContentSourceInfo(preReferences,IndicatorSourceInfoConstant.modifier.name(),name);
        int versionCountEn = versionCountFromIndicatorContentSourceInfo(preReferences,IndicatorSourceInfoConstant.modifierEn.name(),name);
        return currentCount + currentCountEn + versionCount + versionCountEn;
    }


    @Override
    public int indicatorDimensionReferenceCount(String name) {
        List<DssDatamodelIndicatorContent> currentContents = indicatorContentService.sourceInfoReference(name);
        int currentCount = currentCountFromIndicatorContentSourceInfo(IndicatorSourceInfoConstant.dimension,name,currentContents);
        int currentCountEn =  currentCountFromIndicatorContentSourceInfo(IndicatorSourceInfoConstant.dimensionEn,name,currentContents);

        List<DssDatamodelIndicatorVersion> preReferences = indicatorVersionService.contentReferenceCount(name);
        int versionCount = versionCountFromIndicatorContentSourceInfo(preReferences,IndicatorSourceInfoConstant.dimension.name(),name);
        int versionCountEn = versionCountFromIndicatorContentSourceInfo(preReferences,IndicatorSourceInfoConstant.dimensionEn.name(),name);
        return currentCount + currentCountEn + versionCount + versionCountEn;
    }

    @Override
    public int indicatorMeasureReferenceCount(String name) {
        List<DssDatamodelIndicatorContent> currentContents = indicatorContentService.sourceInfoReference(name);
        int currentCount = currentCountFromIndicatorContentSourceInfo(IndicatorSourceInfoConstant.measure,name,currentContents);
        int currentCountEn = currentCountFromIndicatorContentSourceInfo(IndicatorSourceInfoConstant.measureEn,name,currentContents);

        List<DssDatamodelIndicatorVersion> preReferences = indicatorVersionService.contentReferenceCount(name);
        int versionCount = versionCountFromIndicatorContentSourceInfo(preReferences,IndicatorSourceInfoConstant.measure.name(),name);
        int versionCountEn = versionCountFromIndicatorContentSourceInfo(preReferences,IndicatorSourceInfoConstant.measureEn.name(), name);
        return currentCount + currentCountEn + versionCount + versionCountEn;
    }

    @Override
    public int indicatorIndicatorReferenceCount(String name) {
        List<DssDatamodelIndicatorContent> currentContents = indicatorContentService.sourceInfoReference(name);
        int currentCount = currentCountFromIndicatorContentSourceInfo(IndicatorSourceInfoConstant.indicatorName,name,currentContents);
        int currentCountEn = currentCountFromIndicatorContentSourceInfo(IndicatorSourceInfoConstant.indicatorNameEn,name,currentContents);

        List<DssDatamodelIndicatorVersion> preReferences = indicatorVersionService.contentReferenceCount(name);
        int versionCount = versionCountFromIndicatorContentSourceInfo(preReferences,IndicatorSourceInfoConstant.indicatorName.name(),name);
        int versionCountEn = versionCountFromIndicatorContentSourceInfo(preReferences,IndicatorSourceInfoConstant.indicatorNameEn.name(),name);
        return currentCount + currentCountEn + versionCount + versionCountEn;
    }


}
