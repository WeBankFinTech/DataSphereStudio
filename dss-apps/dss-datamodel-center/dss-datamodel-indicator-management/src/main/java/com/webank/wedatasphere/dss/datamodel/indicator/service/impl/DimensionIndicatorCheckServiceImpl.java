package com.webank.wedatasphere.dss.datamodel.indicator.service.impl;


import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.webank.wedatasphere.dss.datamodel.center.common.service.DimensionIndicatorCheckService;
import com.webank.wedatasphere.dss.datamodel.indicator.entity.DssDatamodelIndicatorContent;
import com.webank.wedatasphere.dss.datamodel.indicator.entity.DssDatamodelIndicatorVersion;
import com.webank.wedatasphere.dss.datamodel.indicator.service.IndicatorContentService;
import com.webank.wedatasphere.dss.datamodel.indicator.service.IndicatorService;
import com.webank.wedatasphere.dss.datamodel.indicator.service.IndicatorVersionService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class DimensionIndicatorCheckServiceImpl implements DimensionIndicatorCheckService {

    @Resource
    private IndicatorVersionService indicatorVersionService;

    @Resource
    private IndicatorContentService indicatorContentService;

    @Resource
    private IndicatorService indicatorService;

    @Override
    public Boolean referenceCase(String name) {
        return check(name);
    }

    private Boolean check(String name) {
//        int versionCount = indicatorVersionService.contentReferenceCount(name);// indicatorVersionService.getBaseMapper().selectCount(Wrappers.<DssDatamodelIndicatorVersion>lambdaQuery().like(DssDatamodelIndicatorVersion::getVersionContext, "\""+name + "\""));
//        int contentCount = indicatorContentService.sourceInfoReference(name);//indicatorContentService.getBaseMapper().selectCount(Wrappers.<DssDatamodelIndicatorContent>lambdaQuery().like(DssDatamodelIndicatorContent::getIndicatorSourceInfo, "\""+name + "\""));
//        return versionCount > 0 || contentCount > 0;
        return indicatorService.indicatorDimensionReferenceCount(name)>0;
    }


    @Override
    public Boolean referenceEn(String name) {
        return check(name);
    }
}
