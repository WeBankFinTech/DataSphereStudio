package com.webank.wedatasphere.dss.guide.server.service.impl;

import com.webank.wedatasphere.dss.guide.server.dao.GuideContentMapper;
import com.webank.wedatasphere.dss.guide.server.entity.GuideContent;
import com.webank.wedatasphere.dss.guide.server.service.GuideContentService;
import com.webank.wedatasphere.dss.guide.server.util.GuideException;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;

/**
 * @author suyc @Classname GuideContentServiceImpl @Description TODO @Date 2021/12/21 20:14 @Created
 *     by suyc
 */
@Service
@AllArgsConstructor
public class GuideContentServiceImpl extends ServiceImpl<GuideContentMapper, GuideContent>
        implements GuideContentService {
    private GuideContentMapper guideContentMapper;

    @Override
    public boolean saveGuideContent(GuideContent guideContent) {
        Long id = guideContent.getId();

        if (id != null) {
            return this.updateById(guideContent);
        } else {
            return this.save(guideContent);
        }
    }

    @Override
    public GuideContent getGuideContent(long id) {
        return guideContentMapper.selectById(id);
    }

    @Override
    public List<GuideContent> queryGuideContentByPath(String path) {
        return guideContentMapper.getGuideContentListByPath(path);
    }

    @Override
    public void updateGuideContentById(long id, Map<String, Object> map) throws GuideException {
        Object content = map.get("content");
        if (content == null) {
            throw new GuideException("请设置content参数");
        }
        Object contentHtml = map.get("contentHtml");
        if (contentHtml == null) {
            throw new GuideException("请设置contentHtml参数");
        }
        guideContentMapper.updateGuideContentById(id, content.toString(), contentHtml.toString());
    }

    @Override
    public void deleteGuideContent(Long id) {
        this.removeById(id);
    }
}
