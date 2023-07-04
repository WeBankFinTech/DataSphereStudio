package com.webank.wedatasphere.dss.common.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.webank.wedatasphere.dss.common.entity.NoticeContent;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface NoticeMapper extends BaseMapper<NoticeContent> {
}
