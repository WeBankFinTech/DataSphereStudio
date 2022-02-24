package com.webank.wedatasphere.dss.guide.server.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.webank.wedatasphere.dss.guide.server.dao.GuideChapterMapper;
import com.webank.wedatasphere.dss.guide.server.entity.GuideChapter;
import com.webank.wedatasphere.dss.guide.server.service.GuideChapterService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class GuideChapterServiceImpl extends ServiceImpl<GuideChapterMapper, GuideChapter> implements GuideChapterService {
    private GuideChapterMapper guideChapterMapper;

    @Override
    public boolean saveGuideChapter(GuideChapter guideChapter) {
        Long id = guideChapter.getId();

        if(id != null){
            return this.updateById(guideChapter);
        }
        else {
            return this.save(guideChapter);
        }
    }

    @Override
    public void deleteGuideChapter(Long id) {
        this.removeById(id);
    }

    @Override
    public GuideChapter queryGuideChapterById(Long id) {
        return this.getById(id);
    }

    @Override
    public List<GuideChapter> searchGuideChapterList(String keyword, List<Long> totals, Integer pageNow, Integer pageSize) {
        PageHelper.startPage(pageNow, pageSize, true);
        // MYSQL LIKE % _:  LIKE '%\_%', LIKE '%\%%'
        if(keyword !=null) {
            if ("_".equalsIgnoreCase(keyword.trim())) {
                keyword = "\\_";
            }
            if ("%".equalsIgnoreCase(keyword.trim())) {
                keyword = "\\%";
            }
        }
        List<GuideChapter> guideChapterList = guideChapterMapper.searchGuideChapterListByKeyword(keyword);
        PageInfo<GuideChapter> pageInfo = new PageInfo<>(guideChapterList);
        totals.add(pageInfo.getTotal());

        return guideChapterList;
    }
}
