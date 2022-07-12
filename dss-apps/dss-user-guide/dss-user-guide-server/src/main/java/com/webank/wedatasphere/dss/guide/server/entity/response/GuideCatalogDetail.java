package com.webank.wedatasphere.dss.guide.server.entity.response;

import com.webank.wedatasphere.dss.guide.server.entity.GuideCatalog;
import com.webank.wedatasphere.dss.guide.server.entity.GuideChapter;
import lombok.Data;

import java.util.List;

@Data
public class GuideCatalogDetail {
    private Long id;

    private List<GuideCatalog> childrenCatalog;
    private List<GuideChapter> childrenChapter;
}
