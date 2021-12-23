package com.webank.wedatasphere.dss.guide.server.service;

import com.webank.wedatasphere.dss.guide.server.entity.GuideContent;
import com.webank.wedatasphere.dss.guide.server.entity.GuideGroup;
import com.webank.wedatasphere.dss.guide.server.util.GuideException;

import java.util.List;
import java.util.Map;

/**
 * @author suyc
 * @Classname GuideContentService
 * @Description TODO
 * @Date 2021/12/21 20:11
 * @Created by suyc
 */
public interface GuideContentService {
    public boolean saveGuideContent(GuideContent guideGroup);

    public GuideContent getGuideContent(long id);

    public List<GuideContent> queryGuideContentByPath(String path);

    public void updateContentById(long id, Map<String, Object> map) throws GuideException;

    public void deleteContent(Long id);
}
