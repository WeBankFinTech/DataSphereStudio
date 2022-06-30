package com.webank.wedatasphere.dss.guide.server.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.webank.wedatasphere.dss.guide.server.conf.GuideConf;
import com.webank.wedatasphere.dss.guide.server.dao.GuideCatalogMapper;
import com.webank.wedatasphere.dss.guide.server.dao.GuideChapterMapper;
import com.webank.wedatasphere.dss.guide.server.entity.GuideCatalog;
import com.webank.wedatasphere.dss.guide.server.entity.GuideChapter;
import com.webank.wedatasphere.dss.guide.server.entity.response.GuideCatalogDetail;
import com.webank.wedatasphere.dss.guide.server.service.GuideCatalogService;
import com.webank.wedatasphere.dss.guide.server.util.GuideException;
import com.webank.wedatasphere.dss.guide.server.util.MdAnalysis;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class GuideCatalogServiceImpl extends ServiceImpl<GuideCatalogMapper, GuideCatalog> implements GuideCatalogService {

    private static final Logger logger = LoggerFactory.getLogger(GuideCatalogServiceImpl.class);
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void syncKnowledge(String summaryPath, String ignoreModel) throws Exception {
        logger.info("====================初始化==================");
        chapterInit();
        logger.info("summary文件路径：" + summaryPath);
        //父级目录
        String parentPath = new File(summaryPath).getParent();
        logger.info("父级路径：" + parentPath);
        List<GuideCatalog> catalogs = null;
        List<GuideChapter> chapters = null;
        try {
            //1.解析SUMMARY.md文件
            List<Map<String, Map<String, String>>> maps = MdAnalysis.analysisMd(summaryPath,"knowledge", ignoreModel);
            //2.解析maps并插入dss_guide_catalog表
            catalogs = new ArrayList<>();
            chapters = new ArrayList<>();
            for (Map<String, Map<String, String>> map : maps) {
                for (Map.Entry tag : map.entrySet()) {
                    //可以转为int类型则为一级标题
                    if (MdAnalysis.isStr2Num(String.valueOf(tag.getKey()))) {
                        //参数组装
                        catalogs.add(buildParams(map, tag, Long.valueOf(tag.getKey().toString()), -1l));
                    }

                    //解析二级标题
                    if (MdAnalysis.isSecondOrThird(String.valueOf(tag.getKey())) == 1) {
                        //[1-1, 1-2, 1-3, 1-4, 1-5, 1-6, 1-7, 1-8, 1-9, 1-10, 2-1, 2-2, 3-1, 3-2, 3-3]
                        String secondTag = String.valueOf(tag.getKey());
                        //将split[1]作为id,split[0]作为parentId
                        String[] split = secondTag.split("-");
                        //参数组装
                        if (null != map.get(tag.getKey()).get("file")) {
                            String mdFilePath = parentPath + map.get(tag.getKey()).get("file");
                            chapters.add(buildChapterParams(map, tag, Long.valueOf(split[0]), mdFilePath));
                        } else {
                            catalogs.add(buildParams(map, tag, Long.valueOf(split[1]) + MdAnalysis.getRootCount(), Long.valueOf(split[0])));
                        }
                    }

                    //解析三级标题
                    if (MdAnalysis.isSecondOrThird(String.valueOf(tag.getKey())) == 2) {
                        //[1-1-1, 1-1-2, 1-1-3, 1-1-4, 1-2-1, 1-2-2, 1-2-3, 1-2-4, 1-2-5, 1-2-6, 1-2-7, 1-2-8, 1-2-9, 1-2-10, 1-2-11, 1-2-12, 1-2-13, 1-2-14, 1-2-15, 1-2-16, 1-2-17, 1-2-18, 1-2-19, 1-2-20, 1-2-21, 1-2-22, 1-2-23, 1-2-24, 1-2-25, 1-2-26, 1-3-1, 1-4-1, 1-4-2, 1-4-3, 1-4-4, 1-4-5, 1-4-6, 1-4-7, 1-4-8, 1-4-9, 1-4-10, 1-4-11, 1-4-12, 1-4-13, 1-4-14, 1-4-15, 1-4-16, 1-4-17, 1-4-18, 1-5-1, 1-5-2, 1-5-3, 1-5-4, 1-5-5, 1-5-6, 1-5-7, 1-5-8, 1-5-9, 1-5-10, 1-5-11, 1-5-12, 1-5-13, 1-5-14, 1-5-15, 1-5-16, 1-6-1, 1-6-2, 1-6-3, 1-6-4, 1-6-5, 1-6-6, 1-7-1, 1-7-2, 1-7-3, 1-7-4, 1-7-5, 1-7-6, 1-7-7, 1-7-8, 1-7-9, 1-7-10, 1-7-11, 1-7-12, 1-7-13, 1-7-14, 1-8-1, 1-8-2, 1-8-3, 1-8-4, 1-8-5, 1-9-1, 1-9-2, 1-9-3]
                        String secondTag = String.valueOf(tag.getKey());
                        //split[1]作为catalogId
                        String[] split = secondTag.split("-");
                        //获取summary.md文件中的md文件
                        String mdFilePath = parentPath + map.get(tag.getKey()).get("file");
                        //组装参数存入dss_guide_chapter表
                        chapters.add(buildChapterParams(map, tag, Long.valueOf(split[1]) + MdAnalysis.getRootCount(), mdFilePath));
                    }
                }
            }
        } catch (Exception e) {
            logger.error("服务异常："+ e);
            throw new RuntimeException(e);
        }

        logger.info("catalogs=======>>>>"+ catalogs);
        if (!CollectionUtils.isEmpty(catalogs)) {
            //批量插入到dss_guide_catalog表
            logger.info("=========开始批量插入dss_guide_catalog表============");
            int flag = guideCatalogMapper.batchInsert(catalogs);
            if(flag == 0){
                logger.info("=========批量插入失败dss_guide_catalog表============");
                throw new GuideException("批量插入失败！");
            }
        }
        logger.info("chapters=======>>>>"+ chapters);
        if (!CollectionUtils.isEmpty(chapters)) {
            //批量插入到dss_guide_chapter表
            logger.info("=========开始批量插入dss_guide_chapter表============");
            int flag = guideChapterMapper.batchInsert(chapters);
            if(flag == 0){
                logger.info("=========批量插入失败dss_guide_chapter表============");
                throw new GuideException("批量插入失败！");
            }
        }
        logger.info("知识库同步完成。。。");
    }

    private GuideCatalog buildParams(Map<String, Map<String, String>> map, Map.Entry tag, Long id, Long parentId) {
        GuideCatalog guideCatalog = new GuideCatalog();
        guideCatalog.setId(id);
        guideCatalog.setParentId(parentId);
        guideCatalog.setTitle(map.get(tag.getKey()).get("title"));
        guideCatalog.setCreateTime(new Date());
        guideCatalog.setUpdateTime(new Date());
        return guideCatalog;
    }

    private GuideChapter buildChapterParams(Map<String, Map<String, String>> map, Map.Entry tag, Long parentId, String mdFilePath) throws IOException {
        GuideChapter guideChapter = new GuideChapter();
        guideChapter.setCatalogId(parentId);
        guideChapter.setTitle(map.get(tag.getKey()).get("title"));
        if (!StringUtils.isEmpty(mdFilePath)) {
            logger.info("GuideChapter文件路径："+ mdFilePath);
            String content = MdAnalysis.readMd(mdFilePath);
            guideChapter.setContent(content);
            guideChapter.setContentHtml(MdAnalysis.changeHtmlTagA(MdAnalysis.markdown2Html(mdFilePath)));
        }
        guideChapter.setUpdateTime(new Date());
        guideChapter.setCreateTime(new Date());
        return guideChapter;
    }

    private void chapterInit(){
        //初始化dss_guide_catalog表
        guideCatalogMapper.delete(null);
        //初始化dss_guide_chapter表
        guideChapterMapper.delete(null);
        //初始化dss_guide_chapter表id
        guideChapterMapper.initChapterId();
        MdAnalysis.rootCountInit();
    }
}
