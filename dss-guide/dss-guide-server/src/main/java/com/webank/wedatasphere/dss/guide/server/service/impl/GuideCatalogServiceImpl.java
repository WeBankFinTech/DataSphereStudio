package com.webank.wedatasphere.dss.guide.server.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.webank.wedatasphere.dss.guide.server.dao.GuideCatalogMapper;
import com.webank.wedatasphere.dss.guide.server.dao.GuideChapterMapper;
import com.webank.wedatasphere.dss.guide.server.entity.GuideCatalog;
import com.webank.wedatasphere.dss.guide.server.entity.response.GuideCatalogDetail;
import com.webank.wedatasphere.dss.guide.server.service.GuideCatalogService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author suyc
 * @Classname GuideCatalogServiceImpl
 * @Description TODO
 * @Date 2022/1/14 13:56
 * @Created by suyc
 */
@Service
@AllArgsConstructor
public class GuideCatalogServiceImpl extends ServiceImpl<GuideCatalogMapper, GuideCatalog> implements GuideCatalogService {
    private GuideCatalogMapper guideCatalogMapper;
    private GuideChapterMapper guideChapterMapper;

    @Override
    public boolean saveGuideCatalog(GuideCatalog guideCatalog) {
        Long id = guideCatalog.getId();

        if(id != null){
            return this.updateById(guideCatalog);
        }
        else {
            return this.save(guideCatalog);
        }
    }

    @Override
    public void deleteGuideCatalog(Long id) {
        this.removeById(id);
    }

    @Override
    public List<GuideCatalog> queryGuideCatalogListForTop() {
        return guideCatalogMapper.queryGuideCatalogListForTop();
    }

    @Override
    public GuideCatalogDetail queryGuideCatalogDetailById(Long id) {
        GuideCatalogDetail guideCatalogDetail =new GuideCatalogDetail();

        guideCatalogDetail.setId(id);
        guideCatalogDetail.setChildrenCatalog(guideCatalogMapper.queryGuideCatalogChildrenById(id));
        guideCatalogDetail.setChildrenChapter(guideChapterMapper.queryGuideChapterListByCatalogId(id));

        return guideCatalogDetail;
    }


}
