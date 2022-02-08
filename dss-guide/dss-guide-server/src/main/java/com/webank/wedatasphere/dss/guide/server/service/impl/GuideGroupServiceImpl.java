package com.webank.wedatasphere.dss.guide.server.service.impl;

import com.webank.wedatasphere.dss.guide.server.dao.GuideGroupMapper;
import com.webank.wedatasphere.dss.guide.server.entity.GuideGroup;
import com.webank.wedatasphere.dss.guide.server.service.GuideGroupService;

import org.springframework.stereotype.Service;

import java.util.List;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;

/**
 * @author suyc @Classname GuideGroupServiceImpl @Description TODO @Date 2021/12/21 14:04 @Created
 *     by suyc
 */
@Service
@AllArgsConstructor
public class GuideGroupServiceImpl extends ServiceImpl<GuideGroupMapper, GuideGroup>
        implements GuideGroupService {
    private GuideGroupMapper guideGroupMapper;

    @Override
    public boolean saveGuideGroup(GuideGroup guideGroup) {
        Long id = guideGroup.getId();

        if (id != null) {
            return this.updateById(guideGroup);
        } else {
            return this.save(guideGroup);
        }
    }

    @Override
    public GuideGroup queryGuideGroupByPath(String path) {
        return guideGroupMapper.getGuideGroupByPath(path);
    }

    @Override
    public List<GuideGroup> getAllGuideGroupDetails() {
        return guideGroupMapper.getAllGuideGroupDetails();
    }

    @Override
    public void deleteGuideGroup(Long id) {
        this.removeById(id);
    }
}
