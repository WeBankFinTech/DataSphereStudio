package com.webank.wedatasphere.dss.common.entity;

import java.util.List;

/**
 * 分页结果通用类
 * Author: xlinliu
 * Date: 2023/4/20
 */
public class PageInfo<T> {
    List<T> data;
    Integer total;

    public PageInfo(List<T> data, Integer total) {
        this.data = data;
        this.total = total;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }
}