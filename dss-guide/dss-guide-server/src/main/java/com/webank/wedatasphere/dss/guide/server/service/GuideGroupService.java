package com.webank.wedatasphere.dss.guide.server.service;

import com.webank.wedatasphere.dss.guide.server.entity.GuideGroup;

import java.util.List;

/**
 * @author suyc
 * @Classname GuideGroupService
 * @Description TODO
 * @Date 2021/12/21 14:03
 * @Created by suyc
 */
public interface GuideGroupService {
    public boolean saveGuideGroup(GuideGroup guideGroup);

    public GuideGroup queryGuideGroupByPath(String path);

    public List<GuideGroup> getAllGuideGroupDetails();

    public void deleteGuideGroup(Long id);
}
