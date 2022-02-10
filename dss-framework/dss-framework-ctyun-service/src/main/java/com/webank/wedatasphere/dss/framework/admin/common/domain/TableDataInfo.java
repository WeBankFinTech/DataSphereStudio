package com.webank.wedatasphere.dss.framework.admin.common.domain;

import java.io.Serializable;
import java.util.HashMap;

public class TableDataInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 总记录数
     */
    private long total;

    /**
     * 列表数据
     */
    private HashMap<String,Object > data;

    /**
     * 消息状态码
     */
    private int status;

    /**
     * 消息内容
     */
    private String message;

    /**
     * 表格数据对象
     */
    public TableDataInfo() {
    }

    /**
     * 分页
     *
     * @param data  列表数据
     * @param total 总记录数
     */
    public TableDataInfo(HashMap<String,Object > data, int total) {
        this.data = data;
        this.total = total;
    }



    public HashMap<String,Object > getData() {
        return data;
    }

    public void setData(HashMap<String,Object > data) {
        this.data = data;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

