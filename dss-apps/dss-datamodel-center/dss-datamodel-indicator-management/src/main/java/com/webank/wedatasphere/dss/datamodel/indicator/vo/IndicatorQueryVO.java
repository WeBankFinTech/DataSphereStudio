package com.webank.wedatasphere.dss.datamodel.indicator.vo;

/**
 * @author helong
 * @date 2021/9/16
 */
public class IndicatorQueryVO {

    private Integer pageSize = 10;

    private Integer pageNum = 1;

    private String name;

    private Integer isAvailable;

    private String owner;

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(Integer isAvailable) {
        this.isAvailable = isAvailable;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}
