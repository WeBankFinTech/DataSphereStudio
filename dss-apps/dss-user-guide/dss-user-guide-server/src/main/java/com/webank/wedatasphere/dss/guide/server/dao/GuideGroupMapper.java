package com.webank.wedatasphere.dss.guide.server.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.webank.wedatasphere.dss.guide.server.entity.GuideGroup;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GuideGroupMapper extends BaseMapper<GuideGroup> {
    GuideGroup getGuideGroupByPath(@Param("path") String path);

    List<GuideGroup> getAllGuideGroupDetails();

    int batchInsert(@Param("list") List<GuideGroup> list);

//    @Update("UPDATE dss_guide_group SET `is_delete` = 1,`update_time` = NOW() WHERE `id` = #{id}")
//    void deleteGuideGroup(@Param("id") Long id);
}
