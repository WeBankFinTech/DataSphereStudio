package com.webank.wedatasphere.dss.guide.server.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.webank.wedatasphere.dss.guide.server.entity.GuideCatalog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author suyc
 * @Classname GuideCatalogMapper
 * @Description TODO
 * @Date 2022/1/13 20:30
 * @Created by suyc
 */
@Mapper
public interface GuideCatalogMapper extends BaseMapper<GuideCatalog> {
    /**
     * parent_id =-1 标识该目录属于最顶层的一级目录
     */
    @Select("SELECT * FROM dss_guide_catalog WHERE is_delete =0 AND parent_id =-1 ORDER BY id ASC")
    List<GuideCatalog> queryGuideCatalogListForTop();

    @Select("SELECT * FROM dss_guide_catalog WHERE is_delete =0 AND parent_id =#{id} ORDER BY id ASC")
    List<GuideCatalog> queryGuideCatalogChildrenById(@Param("id") Long id);
}
