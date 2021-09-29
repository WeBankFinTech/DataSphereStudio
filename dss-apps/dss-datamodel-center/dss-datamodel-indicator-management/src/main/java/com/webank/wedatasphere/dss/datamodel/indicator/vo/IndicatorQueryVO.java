package com.webank.wedatasphere.dss.datamodel.indicator.vo;


public class IndicatorQueryVO {

    private Integer pageSize = 10;

    private Integer pageNum = 1;

    private String name;

    private Integer indicatorType;

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

    public Integer getIndicatorType() {
        return indicatorType;
    }

    public void setIndicatorType(Integer indicatorType) {
        this.indicatorType = indicatorType;
    }

    @Override
    public String toString() {
        return "IndicatorQueryVO{" +
                "pageSize=" + pageSize +
                ", pageNum=" + pageNum +
                ", name='" + name + '\'' +
                ", indicatorType=" + indicatorType +
                ", isAvailable=" + isAvailable +
                ", owner='" + owner + '\'' +
                '}';
    }
}
