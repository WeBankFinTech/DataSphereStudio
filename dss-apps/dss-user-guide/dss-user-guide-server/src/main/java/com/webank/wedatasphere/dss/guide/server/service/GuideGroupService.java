package com.webank.wedatasphere.dss.guide.server.service;

import com.webank.wedatasphere.dss.guide.server.entity.GuideGroup;
import com.webank.wedatasphere.dss.guide.server.util.GuideException;

import java.util.List;

public interface GuideGroupService {
    public boolean saveGuideGroup(GuideGroup guideGroup);

    public GuideGroup queryGuideGroupByPath(String path);

    public List<GuideGroup> getAllGuideGroupDetails();

    public void deleteGuideGroup(Long id);

    public void asyncGuide(String summaryPath, String ignoreModel) throws GuideException;
}
