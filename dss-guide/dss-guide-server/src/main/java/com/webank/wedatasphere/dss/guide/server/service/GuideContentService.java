package com.webank.wedatasphere.dss.guide.server.service;

import com.webank.wedatasphere.dss.guide.server.entity.GuideContent;
import com.webank.wedatasphere.dss.guide.server.util.GuideException;

import java.util.List;
import java.util.Map;

public interface GuideContentService {
    public boolean saveGuideContent(GuideContent guideGroup);

    public GuideContent getGuideContent(long id);

    public List<GuideContent> queryGuideContentByPath(String path);

    public void updateGuideContentById(long id, Map<String, Object> map) throws GuideException;

    public void deleteGuideContent(Long id);
}
