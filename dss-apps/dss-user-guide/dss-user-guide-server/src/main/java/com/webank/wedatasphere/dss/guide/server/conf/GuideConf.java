package com.webank.wedatasphere.dss.guide.server.conf;

import org.apache.linkis.common.conf.CommonVars;

public interface GuideConf {
    CommonVars<String> GUIDE_CONTENT_IMAGES_PATH = CommonVars.apply("guide.content.images.path", "/usr/local/anlexander/all_bak/dss_linkis/dss-linkis-1.0.2/images");

    CommonVars<String> GUIDE_CHAPTER_IMAGES_PATH = CommonVars.apply("guide.chapter.images.path", "/usr/local/anlexander/all_bak/dss_linkis/dss-linkis-1.0.2/images");

    /**gitbook路径*/
    CommonVars<String> HOST_GITBOOK_PATH = CommonVars.apply("host.gitbook.path", "/appcom/Install/ApacheInstall/gitbook_books");

    CommonVars<String> HOST_IP_ADDRESS = CommonVars.apply("target.ip.address", "127.0.0.1");

    CommonVars<String> TARGET_GITBOOK_PATH = CommonVars.apply("target.gitbook.path", "/appcom/Install/ApacheInstall/gitbook_books");

    CommonVars<String> SUMMARY_IGNORE_MODEL = CommonVars.apply("summary.ignore.model","km");

    CommonVars<String> GUIDE_SYNC_MODEL = CommonVars.apply("guide.sync.model","gitbook");

}
