package com.webank.wedatasphere.dss.framework.workspace.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.webank.wedatasphere.dss.framework.workspace.bean.NoticeContent;
import com.webank.wedatasphere.dss.framework.workspace.dao.NoticeMapper;
import com.webank.wedatasphere.dss.framework.workspace.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class NoticeServiceImpl implements NoticeService {

    @Autowired
    private NoticeMapper noticeMapper;

    @Override
    public List<NoticeContent> getNoticeContent() {
        QueryWrapper<NoticeContent> queryWrapper = new QueryWrapper<>();
        Date now = new Date();
        queryWrapper.gt("end_time", now);
        queryWrapper.orderByDesc("id");
        return noticeMapper.selectList(queryWrapper);
    }
}
