package com.webank.wedatasphere.dss.datamodel.table.vo;


public class TableListVO {

    private Integer pageSize = 20;

    private Integer pageNum = 1;



    private String name;

    /**
     * 数仓层级
     */
    private String warehouseLayerName;

    /**
     * 数仓主题格式为： theme_domain_name.theme_name
     */
    private String warehouseThemeName;


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

    public String getWarehouseLayerName() {
        return warehouseLayerName;
    }

    public void setWarehouseLayerName(String warehouseLayerName) {
        this.warehouseLayerName = warehouseLayerName;
    }

    public String getWarehouseThemeName() {
        return warehouseThemeName;
    }

    public void setWarehouseThemeName(String warehouseThemeName) {
        this.warehouseThemeName = warehouseThemeName;
    }

    @Override
    public String toString() {
        return "TableListVO{" +
                "pageSize=" + pageSize +
                ", pageNum=" + pageNum +
                ", name='" + name + '\'' +
                ", warehouseLayerName='" + warehouseLayerName + '\'' +
                ", warehouseThemeName='" + warehouseThemeName + '\'' +
                '}';
    }
}
