package com.webank.wedatasphere.warehouse.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.base.Strings;
import com.webank.wedatasphere.dss.datamodel.center.client.impl.LinkisDatamodelCenterRemoteClient;
import com.webank.wedatasphere.dss.datamodel.center.client.request.CyclesReferenceAction;
import com.webank.wedatasphere.dss.datamodel.center.client.request.LayersReferenceAction;
import com.webank.wedatasphere.dss.datamodel.center.client.request.ModifiersReferenceAction;
import com.webank.wedatasphere.dss.datamodel.center.client.request.ThemesReferenceAction;
import com.webank.wedatasphere.dss.datamodel.center.client.response.CyclesReferenceResult;
import com.webank.wedatasphere.dss.datamodel.center.client.response.LayersReferenceResult;
import com.webank.wedatasphere.dss.datamodel.center.client.response.ModifiersReferenceResult;
import com.webank.wedatasphere.dss.datamodel.center.client.response.ThemesReferenceResult;
import com.webank.wedatasphere.warehouse.LinkisRemoteClientHolder;
import com.webank.wedatasphere.warehouse.dao.domain.DwLayer;
import com.webank.wedatasphere.warehouse.dao.domain.DwModifier;
import com.webank.wedatasphere.warehouse.dao.domain.DwStatisticalPeriod;
import com.webank.wedatasphere.warehouse.dao.domain.DwThemeDomain;
import com.webank.wedatasphere.warehouse.dao.mapper.DwLayerMapper;
import com.webank.wedatasphere.warehouse.dao.mapper.DwModifierMapper;
import com.webank.wedatasphere.warehouse.dao.mapper.DwStatisticalPeriodMapper;
import com.webank.wedatasphere.warehouse.dao.mapper.DwThemeDomainMapper;
import com.webank.wedatasphere.warehouse.exception.DwException;
import com.webank.wedatasphere.warehouse.utils.PreconditionUtil;

import java.util.Optional;

public interface DwDomainReferenceAdapter {

    DwStatisticalPeriodMapper getDwStatisticalPeriodMapper();

    DwModifierMapper getDwModifierMapper();

    DwLayerMapper getDwLayerMapper();

    DwThemeDomainMapper getDwThemeDomainMapper();

    default void checkMapper() throws DwException {
//        DwLayerMapper dwLayerMapper = getDwLayerMapper();
//        PreconditionUtil.checkState(Optional.ofNullable(dwLayerMapper).isPresent(), DwException.stateReject("dw layer mapper should not be null"));
//
//        DwThemeDomainMapper dwThemeDomainMapper = getDwThemeDomainMapper();
//        PreconditionUtil.checkState(Optional.ofNullable(dwThemeDomainMapper).isPresent(), DwException.stateReject("dw theme domain mapper should not be null"));
//
//        DwModifierMapper dwModifierMapper = getDwModifierMapper();
//        PreconditionUtil.checkState(Optional.ofNullable(dwModifierMapper).isPresent(), DwException.stateReject("dw modifier mapper should not be null"));
//
//        DwStatisticalPeriodMapper dwStatisticalPeriodMapper = getDwStatisticalPeriodMapper();
//        PreconditionUtil.checkState(Optional.ofNullable(dwStatisticalPeriodMapper).isPresent(), DwException.stateReject("dw statistical period mapper should not be null"));
        checkLayerMapper();

        checkThemeDomainMapper();

        checkModifierMapper();

        checkStatisticalPeriodMapper();
    }

    default void checkModifierMapper() throws DwException {
        DwModifierMapper dwModifierMapper = getDwModifierMapper();
        PreconditionUtil.checkState(Optional.ofNullable(dwModifierMapper).isPresent(), DwException.stateReject("dw modifier mapper should not be null"));
    }

    default void checkLayerMapper() throws DwException {
        DwLayerMapper dwLayerMapper = getDwLayerMapper();
        PreconditionUtil.checkState(Optional.ofNullable(dwLayerMapper).isPresent(), DwException.stateReject("dw layer mapper should not be null"));
    }

    default void checkThemeDomainMapper() throws DwException {
        DwThemeDomainMapper dwThemeDomainMapper = getDwThemeDomainMapper();
        PreconditionUtil.checkState(Optional.ofNullable(dwThemeDomainMapper).isPresent(), DwException.stateReject("dw theme domain mapper should not be null"));
    }

    default void checkStatisticalPeriodMapper() throws DwException {
        DwStatisticalPeriodMapper dwStatisticalPeriodMapper = getDwStatisticalPeriodMapper();
        PreconditionUtil.checkState(Optional.ofNullable(dwStatisticalPeriodMapper).isPresent(), DwException.stateReject("dw statistical period mapper should not be null"));
    }

    // get theme reference count
    default int getThemeDomainReferenceCount(Long id, String username) throws DwException {
        checkThemeDomainMapper();

        DwThemeDomain themeDomain = getDwThemeDomainMapper().selectById(id);
        PreconditionUtil.checkState(Optional.ofNullable(themeDomain).isPresent(), DwException.stateReject("dw theme domain should not be null, id = {}", id));

        LinkisDatamodelCenterRemoteClient dataModelRemoteClient = LinkisRemoteClientHolder.getDataModelRemoteClient();
        ThemesReferenceAction themesReferenceAction = new ThemesReferenceAction.Builder().setName(themeDomain.getEnName()).setUser(username).build();
        ThemesReferenceResult themesReferenceResult = dataModelRemoteClient.themesReference(themesReferenceAction);
        return themesReferenceResult.getResult();
    }

    // get modifier reference count
    default int getModifierReferenceCount(Long id, String username) throws DwException {
        checkModifierMapper();

        DwModifier modifier = getDwModifierMapper().selectById(id);
        PreconditionUtil.checkState(Optional.ofNullable(modifier).isPresent(), DwException.stateReject("dw modifier should not be null, id = {}", id));

        if (Strings.isNullOrEmpty(modifier.getModifierTypeEn())) {
            return 0;
        }
        LinkisDatamodelCenterRemoteClient dataModelRemoteClient = LinkisRemoteClientHolder.getDataModelRemoteClient();
        ModifiersReferenceAction modifiersReferenceAction = new ModifiersReferenceAction.Builder().setName(modifier.getModifierTypeEn()).setUser(username).build();
        ModifiersReferenceResult modifiersReferenceResult = dataModelRemoteClient.modifiersReference(modifiersReferenceAction);
        return modifiersReferenceResult.getResult();
    }

    default int getStatisticalPeriodReferenceCount(Long id, String username) throws DwException {
        checkStatisticalPeriodMapper();

        DwStatisticalPeriod period = getDwStatisticalPeriodMapper().selectById(id);
        PreconditionUtil.checkState(Optional.ofNullable(period).isPresent(), DwException.stateReject("dw statistical period should not be null, id = {}", id));

        // check datamodel
        if (Strings.isNullOrEmpty(period.getEnName())) {
            return 0;
        }
        LinkisDatamodelCenterRemoteClient dataModelRemoteClient = LinkisRemoteClientHolder.getDataModelRemoteClient();
        CyclesReferenceAction cyclesReferenceAction = new CyclesReferenceAction.Builder().setName(period.getEnName()).setUser(username).build();
        CyclesReferenceResult cyclesReferenceResult = dataModelRemoteClient.cyclesReference(cyclesReferenceAction);
        return cyclesReferenceResult.getResult();
    }

    default int getLayerReferenceCount(Long id, String username) throws DwException {
        checkLayerMapper();

        DwLayer dwLayer = getDwLayerMapper().selectById(id);
        PreconditionUtil.checkState(Optional.ofNullable(dwLayer).isPresent(), DwException.stateReject("dw layer should not be null, id = {}", id));

        LinkisDatamodelCenterRemoteClient dataModelRemoteClient = LinkisRemoteClientHolder.getDataModelRemoteClient();
        LayersReferenceAction action = new LayersReferenceAction.Builder().setUser(username).setName(dwLayer.getEnName()).build();
        LayersReferenceResult layersReferenceResult = dataModelRemoteClient.layersReference(action);
        return layersReferenceResult.getResult();
    }

    default boolean isModifierInUse(Long id, String username) throws DwException {
        checkMapper();

        DwModifier modifier = getDwModifierMapper().selectById(id);
        PreconditionUtil.checkState(Optional.ofNullable(modifier).isPresent(), DwException.stateReject("dw modifier should not be null, id = {}", id));

        // check datamodel
        if (!Strings.isNullOrEmpty(modifier.getModifierTypeEn())) {
            LinkisDatamodelCenterRemoteClient dataModelRemoteClient = LinkisRemoteClientHolder.getDataModelRemoteClient();
            ModifiersReferenceAction modifiersReferenceAction = new ModifiersReferenceAction.Builder().setName(modifier.getModifierTypeEn()).setUser(username).build();
            ModifiersReferenceResult modifiersReferenceResult = dataModelRemoteClient.modifiersReference(modifiersReferenceAction);
            int referenceCount = modifiersReferenceResult.getResult();
            return referenceCount > 0;
        }

        return false;
    }

    default boolean isStatisticalPeriodInUse(Long id, String username) throws DwException {
        checkMapper();

        DwStatisticalPeriod period = getDwStatisticalPeriodMapper().selectById(id);
        PreconditionUtil.checkState(Optional.ofNullable(period).isPresent(), DwException.stateReject("dw statistical period should not be null, id = {}", id));

        // check datamodel
        if (!Strings.isNullOrEmpty(period.getEnName())) {
            LinkisDatamodelCenterRemoteClient dataModelRemoteClient = LinkisRemoteClientHolder.getDataModelRemoteClient();
            CyclesReferenceAction cyclesReferenceAction = new CyclesReferenceAction.Builder().setName(period.getEnName()).setUser(username).build();
            CyclesReferenceResult cyclesReferenceResult = dataModelRemoteClient.cyclesReference(cyclesReferenceAction);
            int referenceCount = cyclesReferenceResult.getResult();
            return referenceCount > 0;
        }

        return false;
    }

    default boolean isThemeDomainInUse(Long id, String username) throws DwException {
        checkMapper();

        // 检查修饰词关联数量
        QueryWrapper<DwModifier> queryModifierWrapper = new QueryWrapper<>();
        queryModifierWrapper.eq("theme_domain_id", id);
        queryModifierWrapper.eq("status", Boolean.TRUE);
        queryModifierWrapper.eq("is_available", Boolean.TRUE);
        Integer count1 = getDwModifierMapper().selectCount(queryModifierWrapper);
        boolean isRelModifier = Optional.ofNullable(count1).orElse(0) > 0;

        // 检查统计周期关联数量
        QueryWrapper<DwStatisticalPeriod> statisticalPeriodQueryWrapper = new QueryWrapper<>();
        statisticalPeriodQueryWrapper.eq("theme_domain_id", id);
        statisticalPeriodQueryWrapper.eq("status", Boolean.TRUE);
        statisticalPeriodQueryWrapper.eq("is_available", Boolean.TRUE);
        Integer count2 = getDwStatisticalPeriodMapper().selectCount(statisticalPeriodQueryWrapper);
        boolean isRelStatisticalPeriod = Optional.ofNullable(count2).orElse(0) > 0;

        DwThemeDomain themeDomain = getDwThemeDomainMapper().selectById(id);
        PreconditionUtil.checkState(Optional.ofNullable(themeDomain).isPresent(), DwException.stateReject("dw theme domain should not be null, id = {}", id));

        // check datamodel
        LinkisDatamodelCenterRemoteClient dataModelRemoteClient = LinkisRemoteClientHolder.getDataModelRemoteClient();
        ThemesReferenceAction themesReferenceAction = new ThemesReferenceAction.Builder().setName(themeDomain.getEnName()).setUser(username).build();
        ThemesReferenceResult themesReferenceResult = dataModelRemoteClient.themesReference(themesReferenceAction);
        int referenceCount = themesReferenceResult.getResult();
        boolean isDataModelReferenced = referenceCount > 0;

//        LinkisDataAssetsRemoteClient dataAssetsRemoteClient = LinkisRemoteClientHolder.getDataAssetsRemoteClient();

        return isRelModifier || isRelStatisticalPeriod || isDataModelReferenced;
    }

    default boolean isLayerInUse(Long id, String username) throws DwException {
        checkMapper();

        // 检查修饰词关联数量
        QueryWrapper<DwModifier> queryModifierWrapper = new QueryWrapper<>();
        queryModifierWrapper.eq("layer_id", id);
        queryModifierWrapper.eq("status", Boolean.TRUE);
        queryModifierWrapper.eq("is_available", Boolean.TRUE);
        Integer count1 = getDwModifierMapper().selectCount(queryModifierWrapper);
        boolean isRelModifier = Optional.ofNullable(count1).orElse(0) > 0;

        // 检查统计周期关联数量
        QueryWrapper<DwStatisticalPeriod> statisticalPeriodQueryWrapper = new QueryWrapper<>();
        statisticalPeriodQueryWrapper.eq("layer_id", id);
        statisticalPeriodQueryWrapper.eq("status", Boolean.TRUE);
        statisticalPeriodQueryWrapper.eq("is_available", Boolean.TRUE);
        Integer count2 = getDwStatisticalPeriodMapper().selectCount(statisticalPeriodQueryWrapper);
        boolean isRelStatisticalPeriod = Optional.ofNullable(count2).orElse(0) > 0;

        DwLayer dwLayer = getDwLayerMapper().selectById(id);
        PreconditionUtil.checkState(Optional.ofNullable(dwLayer).isPresent(), DwException.stateReject("dw layer should not be null, id = {}", id));

        LinkisDatamodelCenterRemoteClient dataModelRemoteClient = LinkisRemoteClientHolder.getDataModelRemoteClient();
        LayersReferenceAction action = new LayersReferenceAction.Builder().setUser(username).setName(dwLayer.getEnName()).build();
        LayersReferenceResult layersReferenceResult = dataModelRemoteClient.layersReference(action);
        int referenceCount = layersReferenceResult.getResult();
        boolean isDataModelReferenced = referenceCount > 0;

        return isRelModifier || isRelStatisticalPeriod || isDataModelReferenced;
    }

}
