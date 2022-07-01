package com.webank.wedatasphere.dss.guide.server.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.webank.wedatasphere.dss.guide.server.entity.GuideContent;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface GuideContentMapper extends BaseMapper<GuideContent> {
    @Select("SELECT * FROM dss_guide_content WHERE path=#{path} ORDER BY type,seq")
    List<GuideContent> getGuideContentListByPath(@Param("path") String path);

    @Select("SELECT content FROM dss_guide_content WHERE id=#{id}")
    String getGuideContentById(@Param("id") long id);

    @Update("UPDATE dss_guide_content SET content =#{content},content_html =#{contentHtml} WHERE id =#{id}")
    void updateGuideContentById(@Param("id") long id, @Param("content") String content, @Param("contentHtml") String contentHtml);

    int batchInsert(@Param("list") List<GuideContent> list);

    void initContentId();

//    @Update("UPDATE dss_guide_content SET `is_delete` = 1,`update_time` = NOW() WHERE `id` = #{id}")
//    void deleteGuideContent(@Param("id") Long id);
}
