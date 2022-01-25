package com.webank.wedatasphere.dss.framework.admin.pojo.entity;

import java.util.List;

/**
 * @Auther: Han Tang
 * @Date: 2022/1/18-01-18-18:06
 */
public class DssExchangeTaskRes {

    private int page;
    private int totalItems;
    private int totalPages;
    private int pageSize;
    private List<DssExchangeTask> dssExchangeTaskList;
    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public List<DssExchangeTask> getDssExchangeTaskList() {
        return dssExchangeTaskList;
    }

    public void setDssExchangeTaskList(List<DssExchangeTask> dssExchangeTaskList) {
        this.dssExchangeTaskList = dssExchangeTaskList;
    }


}
