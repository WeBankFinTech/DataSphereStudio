package com.webank.wedatasphere.dss.guide.server.conf;

import org.apache.linkis.common.conf.CommonVars;

/**
 * @author suyc
 * @Classname GuideConf
 * @Description TODO
 * @Date 2021/12/23 15:22
 * @Created by suyc
 */
public interface GuideConf {
    /**
     * 页面向导图片-存储位置
      */
    CommonVars<String> GUIDE_PAGE_IMAGES_PATH = CommonVars.apply("guide.page.images.path", "/opt/dss/dss-guide-server/guide_images");
    /**
     * 知识库向导图片-存储位置
      */
    CommonVars<String> GUIDE_KNOWLEDGE_IMAGES_PATH = CommonVars.apply("guide.knowledge.images.path", "/opt/dss/dss-guide-server/guide_images");
}
