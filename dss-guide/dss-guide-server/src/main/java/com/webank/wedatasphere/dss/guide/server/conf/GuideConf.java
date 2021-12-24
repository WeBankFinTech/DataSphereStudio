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
    CommonVars<String> GUIDE_CONTENT_IMAGES_PATH = CommonVars.apply("guide_content_images_path", "/usr/local/anlexander/all_bak/dss_linkis/dss-linkis-1.0.2/images");

}
