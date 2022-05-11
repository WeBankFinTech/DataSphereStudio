package com.webank.wedatasphere.dss.guide.server.service;

import com.webank.wedatasphere.dss.guide.server.entity.GuideCatalog;
import com.webank.wedatasphere.dss.guide.server.entity.response.GuideCatalogDetail;

import java.util.List;

public interface GuideCatalogService {
    public boolean saveGuideCatalog(GuideCatalog guideCatalog);

    public void deleteGuideCatalog(Long id);

    public List<GuideCatalog> queryGuideCatalogListForTop();

    public GuideCatalogDetail queryGuideCatalogDetailById(Long id);

    public void syncKnowledge(String summaryPath, String ignoreModel) throws Exception;
}
