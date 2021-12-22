package com.webank.wedatasphere.dss.guide.server.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.webank.wedatasphere.dss.guide.server.entity.GuideGroup;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * @author suyc
 * @Classname GuideGroupMapper
 * @Description TODO
 * @Date 2021/12/21 13:53
 * @Created by suyc
 */
@Mapper
public interface GuideGroupMapper extends BaseMapper<GuideGroup> {
    @Select("SELECT * FROM dss_guide_group WHERE path = #{path}")
    GuideGroup getGuideGroupByPath(String path);
}
