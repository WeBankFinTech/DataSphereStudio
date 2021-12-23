package com.webank.wedatasphere.dss.guide.server.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.webank.wedatasphere.dss.guide.server.entity.GuideContent;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * @author suyc
 * @Classname GuideContentMapper
 * @Description TODO
 * @Date 2021/12/21 17:02
 * @Created by suyc
 */
@Mapper
public interface GuideContentMapper extends BaseMapper<GuideContent> {
    @Select("SELECT * FROM dss_guide_content WHERE path=#{path} ORDER BY type,seq")
    List<GuideContent> getGuideContentListByPath(String path);

    @Select("SELECT content FROM dss_guide_content WHERE id=#{id}")
    String getContentById(long id);

    @Update("UPDATE dss_guide_content SET content =#{content},content_html =#{contentHtml} WHERE id =#{id}")
    void updateContentById(long id,String content,String contentHtml);
}
