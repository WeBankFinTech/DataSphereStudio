package com.webank.wedatasphere.dss.guide.server.service;

import com.webank.wedatasphere.dss.guide.server.entity.GuideChapter;

import java.util.List;

/**
 * @author suyc
 * @Classname GuideChapterService
 * @Description TODO
 * @Date 2022/1/14 15:33
 * @Created by suyc
 */
public interface GuideChapterService {
    public boolean saveGuideChapter(GuideChapter guideChapter);

    public void deleteGuideChapter(Long id);

    public GuideChapter queryGuideChapterById(Long id);

    public List<GuideChapter> searchGuideChapterList(String keyword, List<Long> totals, Integer pageNow, Integer pageSize);
}
