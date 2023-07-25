package com.webank.wedatasphere.dss.framework.workspace.bean;

import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

/**
 * 首页公告
 * Author: xlinliu
 * Date: 2023/3/13
 */
@TableName(value = "dss_notice")
public class NoticeContent implements Serializable {
    /**
     * 公告内容
     */
    private String content;
    /**
     * 公告生效时间
     */
    private Date startTime;
    /**
     * 公告失效时间
     */
    private Date endTime;



    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
}
