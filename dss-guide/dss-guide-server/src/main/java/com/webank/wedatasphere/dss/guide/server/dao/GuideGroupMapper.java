package com.webank.wedatasphere.dss.guide.server.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.webank.wedatasphere.dss.guide.server.entity.GuideGroup;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * @author suyc
 * @Classname GuideGroupMapper
 * @Description TODO
 * @Date 2021/12/21 13:53
 * @Created by suyc
 */
@Mapper
public interface GuideGroupMapper extends BaseMapper<GuideGroup> {
    GuideGroup getGuideGroupByPath(@Param("path") String path);

    List<GuideGroup> getAllGuideGroupDetails();

    @Update("UPDATE dss_guide_group SET `is_delete` = 1,`update_time` = NOW() WHERE `id` = #{id}")
    void deleteGroup(@Param("id") Long id);
}
