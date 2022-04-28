package com.webank.wedatasphere.dss.guide.server.conf;

import org.apache.linkis.common.conf.CommonVars;

public interface GuideConf {
    CommonVars<String> GUIDE_CONTENT_IMAGES_PATH = CommonVars.apply("guide.content.images.path", "/usr/local/anlexander/all_bak/dss_linkis/dss-linkis-1.0.2/images");

    CommonVars<String> GUIDE_CHAPTER_IMAGES_PATH = CommonVars.apply("guide.chapter.images.path", "/usr/local/anlexander/all_bak/dss_linkis/dss-linkis-1.0.2/images");

    /**gitbook的SUMMARY.md存放路径*/
    CommonVars<String> GUIDE_GITBOOK_SUMMARY_PATH = CommonVars.apply("guide.gitbook.summary.path", "/appcom/Install/ApacheInstall/gitbook_books/SUMMARY.md");

    /**nginx指向图片的路径*/
    CommonVars<String> GUIDE_KNOWLEDGE_IMAGES_PATH = CommonVars.apply("guide.gitbook.summary.path", "/appcom/Install/ApacheInstall/gitbook_books/SUMMARY.md");

}
