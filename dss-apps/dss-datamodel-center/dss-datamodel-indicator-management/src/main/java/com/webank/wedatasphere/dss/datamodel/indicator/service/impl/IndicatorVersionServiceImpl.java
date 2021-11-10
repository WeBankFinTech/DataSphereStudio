package com.webank.wedatasphere.dss.datamodel.indicator.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
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
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;


@Service
public class IndicatorVersionServiceImpl extends ServiceImpl<DssDatamodelIndicatorVersionMapper, DssDatamodelIndicatorVersion> implements IndicatorVersionService {

    private final Gson gson = new Gson();

    @Override
    @Transactional
    public int addOlderVersion(String name, String owner, String principalName, String version, String comment, IndicatorVersionDTO versionContext) throws ErrorException {
        DssDatamodelIndicatorVersion oldVersion = new DssDatamodelIndicatorVersion();
        oldVersion.setName(name);
        oldVersion.setPrincipalName(principalName);
        oldVersion.setOwner(owner);
        oldVersion.setVersion(version);
        oldVersion.setVersionContext(gson.toJson(versionContext));
        oldVersion.setComment(comment);
        oldVersion.setCreateTime(new Date());
        oldVersion.setUpdateTime(new Date());
        if (!save(oldVersion)) {
            throw new DSSDatamodelCenterException(ErrorCode.INDICATOR_VERSION_ADD_ERROR.getCode(), "old version save error");

        }
        return 1;
    }


    @Override
    public String findLastVersion(String name) {
        PageHelper.clearPage();
        PageHelper.startPage(1,1);
        //查询当前指标名称最大版本
        PageInfo<DssDatamodelIndicatorVersion> pageInfo = new PageInfo<>(getBaseMapper().selectList(
                Wrappers.<DssDatamodelIndicatorVersion>lambdaQuery()
                        .eq(DssDatamodelIndicatorVersion::getName, name)
                        .orderByDesc(DssDatamodelIndicatorVersion::getVersion)));

        List<DssDatamodelIndicatorVersion> list = pageInfo.getList();
        return !CollectionUtils.isEmpty(list) ? list.get(0).getVersion() : null;
    }


    @Override
    public DssDatamodelIndicatorVersion findBackup(String name, String version) {
        return getBaseMapper().selectOne(
                Wrappers.<DssDatamodelIndicatorVersion>lambdaQuery()
                        .eq(DssDatamodelIndicatorVersion::getName, name)
                        .eq(DssDatamodelIndicatorVersion::getVersion, version));
    }
}
