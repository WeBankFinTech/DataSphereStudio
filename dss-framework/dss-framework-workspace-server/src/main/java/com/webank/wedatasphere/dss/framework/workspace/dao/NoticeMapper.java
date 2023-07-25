package com.webank.wedatasphere.dss.framework.workspace.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.webank.wedatasphere.dss.framework.workspace.bean.NoticeContent;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface NoticeMapper extends BaseMapper<NoticeContent> {
}
