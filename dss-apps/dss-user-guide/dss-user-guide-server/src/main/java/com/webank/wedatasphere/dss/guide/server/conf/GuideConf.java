package com.webank.wedatasphere.dss.guide.server.conf;

import org.apache.linkis.common.conf.CommonVars;

public interface GuideConf {
    CommonVars<String> GUIDE_CONTENT_IMAGES_PATH = CommonVars.apply("guide.content.images.path", "/usr/local/anlexander/all_bak/dss_linkis/dss-linkis-1.0.2/images");

    CommonVars<String> GUIDE_CHAPTER_IMAGES_PATH = CommonVars.apply("guide.chapter.images.path", "/usr/local/anlexander/all_bak/dss_linkis/dss-linkis-1.0.2/images");
}