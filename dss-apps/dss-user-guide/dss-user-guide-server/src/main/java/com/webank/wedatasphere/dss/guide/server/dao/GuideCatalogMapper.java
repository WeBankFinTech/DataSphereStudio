package com.webank.wedatasphere.dss.guide.server.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.webank.wedatasphere.dss.guide.server.entity.GuideCatalog;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface GuideCatalogMapper extends BaseMapper<GuideCatalog> {
    /**
     * parent_id =-1 标识该目录属于最顶层的一级目录
     */
    @Select("SELECT * FROM dss_guide_catalog WHERE is_delete =0 AND parent_id =-1 ORDER BY id ASC")
    List<GuideCatalog> queryGuideCatalogListForTop();

    @Select("SELECT * FROM dss_guide_catalog WHERE is_delete =0 AND parent_id =#{id} ORDER BY id ASC")
    List<GuideCatalog> queryGuideCatalogChildrenById(@Param("id") Long id);

    @Insert({
            "<script>",
            "insert into dss_guide_catalog",
            "(id,parent_id, title, description, create_by, create_time, update_by, update_time)",
            "values",
            "<foreach collection='list' item='item' open='(' separator='),(' close=')'>",
            "#{item.id},#{item.parentId}, #{item.title}, #{item.description},#{item.createBy}, #{item.createTime}, #{item.updateBy},#{item.updateTime}",
            "</foreach>",
            "</script>"
    })
    int batchInsert(@Param("list") List<GuideCatalog> list);
}
