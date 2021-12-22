package com.webank.wedatasphere.dss.guide.server.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.webank.wedatasphere.dss.guide.server.entity.GuideContent;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

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
}
