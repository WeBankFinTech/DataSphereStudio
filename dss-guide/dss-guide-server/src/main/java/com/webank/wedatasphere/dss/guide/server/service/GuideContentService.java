package com.webank.wedatasphere.dss.guide.server.service;

import com.webank.wedatasphere.dss.guide.server.entity.GuideContent;
import com.webank.wedatasphere.dss.guide.server.entity.GuideGroup;

import java.util.List;

/**
 * @author suyc
 * @Classname GuideContentService
 * @Description TODO
 * @Date 2021/12/21 20:11
 * @Created by suyc
 */
public interface GuideContentService {
    public boolean saveGuideContent(GuideContent guideGroup);

    public List<GuideContent> queryGuideContentByPath(String path);
}
