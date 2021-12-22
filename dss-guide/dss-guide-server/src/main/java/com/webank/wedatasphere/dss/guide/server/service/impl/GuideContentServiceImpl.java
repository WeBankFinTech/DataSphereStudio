package com.webank.wedatasphere.dss.guide.server.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.webank.wedatasphere.dss.guide.server.dao.GuideContentMapper;
import com.webank.wedatasphere.dss.guide.server.entity.GuideContent;
import com.webank.wedatasphere.dss.guide.server.service.GuideContentService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author suyc
 * @Classname GuideContentServiceImpl
 * @Description TODO
 * @Date 2021/12/21 20:14
 * @Created by suyc
 */
@Service
@AllArgsConstructor
public class GuideContentServiceImpl extends ServiceImpl<GuideContentMapper, GuideContent> implements GuideContentService {
    private GuideContentMapper guideContentMapper;

    @Override
    public boolean saveGuideContent(GuideContent guideContent) {
        Long id = guideContent.getId();

        if(id != null){
            return this.updateById(guideContent);
        }
        else {
            return this.save(guideContent);
        }
    }

    @Override
    public List<GuideContent> queryGuideContentByPath(String path) {
        return guideContentMapper.getGuideContentListByPath(path);
    }
}
