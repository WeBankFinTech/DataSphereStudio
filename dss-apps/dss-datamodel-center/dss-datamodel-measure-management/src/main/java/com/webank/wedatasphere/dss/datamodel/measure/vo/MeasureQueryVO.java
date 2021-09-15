package com.webank.wedatasphere.dss.datamodel.measure.vo;

/**
 * @author helong
 * @date 2021/9/15
 */
public class MeasureQueryVO {

    private Integer pageSize = 10;

    private Integer pageNum = 1;

    private String name;

    private Integer status;

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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    @Override
    public String toString() {
        return "MeasureQueryVO{" +
                "pageSize=" + pageSize +
                ", pageNum=" + pageNum +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", owner='" + owner + '\'' +
                '}';
    }
}
