package com.webank.wedatasphere.dss.guide.server.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.webank.wedatasphere.dss.guide.server.dao.GuideContentMapper;
import com.webank.wedatasphere.dss.guide.server.dao.GuideGroupMapper;
import com.webank.wedatasphere.dss.guide.server.entity.GuideContent;
import com.webank.wedatasphere.dss.guide.server.entity.GuideGroup;
import com.webank.wedatasphere.dss.guide.server.service.GuideGroupService;
import com.webank.wedatasphere.dss.guide.server.util.GuideException;
import com.webank.wedatasphere.dss.guide.server.util.MdAnalysis;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
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
public class GuideGroupServiceImpl extends ServiceImpl<GuideGroupMapper, GuideGroup> implements GuideGroupService {

    private static final Logger logger = LoggerFactory.getLogger(GuideGroupServiceImpl.class);
    private GuideGroupMapper guideGroupMapper;

    private GuideContentMapper guideContentMapper;

    @Override
    public boolean saveGuideGroup(GuideGroup guideGroup) {
        Long id = guideGroup.getId();

        if(id != null){
            return this.updateById(guideGroup);
        }
        else {
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
    public void deleteGuideGroup(Long id){
        this.removeById(id);
    }

    @Override
    public void asyncGuide(String summaryPath,String ignoreModel) throws GuideException {
        logger.info("====================初始化guide-group guide-content==================");
        guideInit();
        //父级目录
        String parentPath = new File(summaryPath).getParent();
        logger.info("父级路径：" + parentPath);
        List<GuideGroup> guideGroups = new ArrayList<>();
        List<GuideContent> guideContents = new ArrayList<>();
        String index = null;
        try {
            //1.解析SUMMARY.md文件
            List<Map<String, Map<String, String>>> maps = MdAnalysis.analysisMd(summaryPath,"guide",ignoreModel);
            for(Map<String, Map<String, String>> map : maps){
                for (Map.Entry tag : map.entrySet()) {
                    if(!StringUtils.isEmpty(map.get("url"))){
                        index = map.get("url").get("path").trim();
                    }
                    if(MdAnalysis.isStr2Num(String.valueOf(tag.getKey()))){
                        guideGroups.add(buildGroupParams(map,tag,Long.valueOf(tag.getKey().toString()),index));
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
                        guideContents.add(buildContentParams(map, tag, Long.valueOf(split[0]),Integer.valueOf(split[1]), mdFilePath,index));
                    }
                }
            }
        } catch (Exception e) {
            logger.error("服务异常："+ e);
            throw new RuntimeException(e);
        }

        logger.info("guideGroups=======>>>>"+ guideGroups);
        if (!CollectionUtils.isEmpty(guideGroups)) {
            //批量插入到dss_guide_group表
            logger.info("=========开始批量插入dss_guide_group表============");
            int flag = guideGroupMapper.batchInsert(guideGroups);
            if(flag == 0){
                logger.info("=========批量插入失败dss_guide_group表============");
                throw new GuideException("批量插入失败！");
            }
        }
        logger.info("guideContents=======>>>>"+ guideContents);
        if (!CollectionUtils.isEmpty(guideContents)) {
            //批量插入到dss_guide_content表
            logger.info("=========开始批量插入dss_guide_content表============");
            int flag = guideContentMapper.batchInsert(guideContents);
            if(flag == 0){
                logger.info("=========批量插入失败dss_guide_content表============");
                throw new GuideException("批量插入失败！");
            }
        }
        logger.info("学习引导同步完成！！！");
    }

    private GuideContent buildContentParams(Map<String, Map<String, String>> map, Map.Entry tag, long groupId, int type, String mdFilePath, String index) throws IOException {
        GuideContent guideContent = new GuideContent();
        if (!StringUtils.isEmpty(mdFilePath)) {
            String content = MdAnalysis.readMd(mdFilePath);
            guideContent.setContent(content);
            guideContent.setContentHtml(MdAnalysis.changeHtmlTagA(MdAnalysis.markdown2Html(mdFilePath)));
        }
        guideContent.setType(type);
        guideContent.setTitle(map.get(tag.getKey()).get("title"));
        guideContent.setGroupId(groupId);
        guideContent.setPath(index);
        guideContent.setCreateTime(new Date());
        guideContent.setUpdateTime(new Date());
        guideContent.setUpdateBy("system");
        guideContent.setCreateBy("system");
        guideContent.setIsDelete(0);
        return guideContent;
    }


    private GuideGroup buildGroupParams(Map<String, Map<String, String>> map,Map.Entry tag, Long id,String index){
        GuideGroup guideGroup = new GuideGroup();
        guideGroup.setId(id);
        guideGroup.setTitle(map.get(tag.getKey()).get("title"));
        guideGroup.setCreateTime(new Date());
        guideGroup.setUpdateTime(new Date());
        guideGroup.setCreateBy("system");
        guideGroup.setUpdateBy("system");
        guideGroup.setPath(index);
        guideGroup.setIsDelete(0);
        return guideGroup;
    }

    private void guideInit(){
        guideGroupMapper.delete(null);
        guideContentMapper.delete(null);
        //初始化dss_guide_chapter表id
        guideContentMapper.initContentId();
        MdAnalysis.rootCountInit();
    }
}
