package com.webank.wedatasphere.dss.guide.server.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.webank.wedatasphere.dss.guide.server.entity.GuideChapter;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import javax.ws.rs.QueryParam;
import java.util.List;

@Mapper
public interface GuideChapterMapper extends BaseMapper<GuideChapter> {
    @Select("SELECT * FROM dss_guide_chapter WHERE is_delete =0 AND catalog_id =#{catalogId} ORDER BY id ASC")
    List<GuideChapter> queryGuideChapterListByCatalogId(@Param("catalogId") Long catalogId);

    @Select("SELECT * FROM dss_guide_chapter WHERE is_delete =0 AND (content LIKE CONCAT('%', #{keyword}, '%') OR title LIKE CONCAT('%', #{keyword}, '%')) ORDER BY id ASC")
    List<GuideChapter> searchGuideChapterListByKeyword(@Param("keyword") String keyword);

    int batchInsert(@Param("list") List<GuideChapter> list);

    @Select("truncate table dss_guide_chapter;")
    void initChapterId();
}
