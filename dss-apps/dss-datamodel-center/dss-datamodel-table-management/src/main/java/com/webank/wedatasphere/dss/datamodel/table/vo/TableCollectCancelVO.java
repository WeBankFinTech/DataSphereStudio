package com.webank.wedatasphere.dss.datamodel.table.vo;


public class TableCollectCancelVO {

    private String user;

    private String tableName;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public String toString() {
        return "TableCollectCancelVO{" +
                "user='" + user + '\'' +
                ", tableName='" + tableName + '\'' +
                '}';
    }
}
