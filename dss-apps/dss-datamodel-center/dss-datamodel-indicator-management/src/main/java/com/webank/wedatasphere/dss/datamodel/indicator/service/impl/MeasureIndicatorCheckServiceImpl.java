package com.webank.wedatasphere.dss.datamodel.indicator.service.impl;


import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.webank.wedatasphere.dss.datamodel.center.common.service.MeasureIndicatorCheckService;
import com.webank.wedatasphere.dss.datamodel.indicator.entity.DssDatamodelIndicatorContent;
import com.webank.wedatasphere.dss.datamodel.indicator.entity.DssDatamodelIndicatorVersion;
import com.webank.wedatasphere.dss.datamodel.indicator.service.IndicatorContentService;
import com.webank.wedatasphere.dss.datamodel.indicator.service.IndicatorVersionService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class MeasureIndicatorCheckServiceImpl implements MeasureIndicatorCheckService {
    @Resource
    private IndicatorVersionService indicatorVersionService;

    @Resource
    private IndicatorContentService indicatorContentService;

    @Override
    public Boolean referenceCase(String name) {
        int versionCount = indicatorVersionService.getBaseMapper().selectCount(Wrappers.<DssDatamodelIndicatorVersion>lambdaQuery().like(DssDatamodelIndicatorVersion::getVersionContext, "\""+name + "\""));
        int contentCount = indicatorContentService.getBaseMapper().selectCount(Wrappers.<DssDatamodelIndicatorContent>lambdaQuery().like(DssDatamodelIndicatorContent::getIndicatorSourceInfo, "\""+name + "\""));
        return versionCount > 0 || contentCount > 0;
    }
}
