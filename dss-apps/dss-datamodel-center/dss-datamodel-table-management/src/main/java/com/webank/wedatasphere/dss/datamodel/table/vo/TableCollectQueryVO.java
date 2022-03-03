package com.webank.wedatasphere.dss.datamodel.table.vo;



public class TableCollectQueryVO {

    private Integer pageSize = 20;

    private Integer pageNum = 1;


    private String dataBase;


    private String name;

    /**
     * 数仓层级
     */
    private String warehouseLayerName;

    /**
     * 数仓主题格式为： theme_domain_name.theme_name
     */
    private String warehouseThemeName;


    private String user;


    public String getDataBase() {
        return dataBase;
    }

    public void setDataBase(String dataBase) {
        this.dataBase = dataBase;
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

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

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

    @Override
    public String toString() {
        return "TableCollectQueryVO{" +
                "pageSize=" + pageSize +
                ", pageNum=" + pageNum +
                ", dataBase='" + dataBase + '\'' +
                ", name='" + name + '\'' +
                ", warehouseLayerName='" + warehouseLayerName + '\'' +
                ", warehouseThemeName='" + warehouseThemeName + '\'' +
                ", user='" + user + '\'' +
                '}';
    }
}
