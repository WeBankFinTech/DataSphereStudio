package com.webank.wedatasphere.dss.datamodel.table.vo;


public class TableDictionaryListVO {

    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "TableDictionaryListVO{" +
                "type='" + type + '\'' +
                '}';
    }
}
