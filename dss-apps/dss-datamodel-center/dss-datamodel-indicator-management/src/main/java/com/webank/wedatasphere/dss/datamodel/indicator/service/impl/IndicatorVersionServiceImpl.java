package com.webank.wedatasphere.dss.datamodel.indicator.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.webank.wedatasphere.dss.datamodel.center.common.constant.ErrorCode;
import com.webank.wedatasphere.dss.datamodel.center.common.exception.DSSDatamodelCenterException;
import com.webank.wedatasphere.dss.datamodel.indicator.dao.DssDatamodelIndicatorVersionMapper;
import com.webank.wedatasphere.dss.datamodel.indicator.dto.IndicatorVersionDTO;
import com.webank.wedatasphere.dss.datamodel.indicator.entity.DssDatamodelIndicatorVersion;
import com.webank.wedatasphere.dss.datamodel.indicator.service.IndicatorVersionService;
import com.webank.wedatasphere.linkis.common.exception.ErrorException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * @author helong
 * @date 2021/9/17
 */
@Service
public class IndicatorVersionServiceImpl extends ServiceImpl<DssDatamodelIndicatorVersionMapper, DssDatamodelIndicatorVersion> implements IndicatorVersionService {

    private final Gson gson = new Gson();

    @Override
    @Transactional
    public int addOlderVersion(String name, String principalName, String version, String comment, IndicatorVersionDTO versionContext) throws ErrorException {
        DssDatamodelIndicatorVersion oldVersion = new DssDatamodelIndicatorVersion();
        oldVersion.setName(name);
        oldVersion.setPrincipalName(principalName);
        oldVersion.setVersion(version);
        oldVersion.setVersionContext(gson.toJson(versionContext));
        oldVersion.setComment(comment);
        oldVersion.setCreateTime(new Date());
        oldVersion.setUpdateTime(new Date());
        if (save(oldVersion)) {
            throw new DSSDatamodelCenterException(ErrorCode.INDICATOR_VERSION_ADD_ERROR.getCode(), "old verison save error");

        }
        return 1;
    }


    @Override
    public String findLastVersion(String name) {
        //查询当前指标名称最大版本
        DssDatamodelIndicatorVersion lastVersion = getBaseMapper().selectOne(
                Wrappers.<DssDatamodelIndicatorVersion>lambdaQuery()
                        .eq(DssDatamodelIndicatorVersion::getName, name)
                        .orderByDesc(DssDatamodelIndicatorVersion::getVersion));

        return lastVersion != null ? lastVersion.getVersion() : null;
    }


    @Override
    public DssDatamodelIndicatorVersion findBackup(String name, String version) {
        return getBaseMapper().selectOne(
                Wrappers.<DssDatamodelIndicatorVersion>lambdaQuery()
                        .eq(DssDatamodelIndicatorVersion::getName, name)
                        .eq(DssDatamodelIndicatorVersion::getVersion, version));
    }
}
