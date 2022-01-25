package com.webank.wedatasphere.dss.guide.server.service;

import com.webank.wedatasphere.dss.guide.server.entity.GuideCatalog;
import com.webank.wedatasphere.dss.guide.server.entity.response.GuideCatalogDetail;

import java.util.List;

/**
 * @author suyc
 * @Classname GuideCatalogService
 * @Description TODO
 * @Date 2022/1/14 11:07
 * @Created by suyc
 */
public interface GuideCatalogService {
    public boolean saveGuideCatalog(GuideCatalog guideCatalog);

    public void deleteGuideCatalog(Long id);

    public List<GuideCatalog> queryGuideCatalogListForTop();

    public GuideCatalogDetail queryGuideCatalogDetailById(Long id);
}
