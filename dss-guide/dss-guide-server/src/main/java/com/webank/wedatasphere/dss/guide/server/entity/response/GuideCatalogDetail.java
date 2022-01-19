package com.webank.wedatasphere.dss.guide.server.entity.response;

import com.webank.wedatasphere.dss.guide.server.entity.GuideCatalog;
import com.webank.wedatasphere.dss.guide.server.entity.GuideChapter;
import lombok.Data;

import java.util.List;

/**
 * @author suyc
 * @Classname GuideCatalogDetail
 * @Description TODO
 * @Date 2022/1/14 15:18
 * @Created by suyc
 */
@Data
public class GuideCatalogDetail {
    private Long id;

    private List<GuideCatalog> childrenCatalog;
    private List<GuideChapter> childrenChapter;
}
