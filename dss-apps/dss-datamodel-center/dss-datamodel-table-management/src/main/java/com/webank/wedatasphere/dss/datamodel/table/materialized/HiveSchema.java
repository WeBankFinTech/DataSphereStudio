package com.webank.wedatasphere.dss.datamodel.table.materialized;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class HiveSchema {

    private Map<String, Column> columnMap = new HashMap<String, Column>();
    private List<Column> columnList = new ArrayList<Column>();
    private String tableName;
    private String dataBase;
    private String originalDataFormat;
    private String storedType;
    private boolean isExternal =false;
    private String location;
    private String comment;
    public List<Column> partitionList = new ArrayList<Column>();

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setExternal(boolean external) {
        isExternal = external;
    }

    public void setStoredType(String storedType) {
        this.storedType = storedType;
    }

    public String getStoredType() {
        return storedType;
    }

    public boolean isExternal() {
        return isExternal;
    }

    public String getDataBase() {
        return dataBase;
    }

    public void setDataBase(String dataBase) {
        this.dataBase = dataBase;
    }

    public List<Column> getPartitionColumns() {
        return partitionList;
    }

    public List<Column> getColumns() {
        return columnList;
    }

    public Column getColumn(String name) {
        return columnMap.get(name);
    }


    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getTableName() {
        return tableName;
    }


    public String getOriginalDataFormat() {
        return originalDataFormat;
    }

    public void setOriginalDataFormat(String originalDataFormat) {
        this.originalDataFormat = originalDataFormat;
    }

    public void addColumn(String name, String type, int length, int percision, boolean isPartition, boolean isPrimaryKey,String comment) {
        Column column = new Column(name, type, length, percision, isPartition, isPrimaryKey,comment);
        columnList.add(column);
        columnMap.put(name, column);
        if (isPartition) {
            partitionList.add(column);
        }
    }


    public static class Column {
        String name;
        String type;
        int length;
        int percision;
        private String comment;
        boolean isPartition;
        boolean isPrimaryKey;

        public Column(String name, String type, int length, int percision, boolean isPartition, boolean isPrimaryKey,String comment) {
            super();
            this.name = name;
            this.type = type;
            this.length = length;
            this.percision = percision;
            this.isPartition = isPartition;
            this.isPrimaryKey = isPrimaryKey;
            this.comment = comment;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public int getLength() {
            return length;
        }

        public void setLength(int length) {
            this.length = length;
        }

        public int getPercision() {
            return percision;
        }

        public void setPercision(int percision) {
            this.percision = percision;
        }

        public boolean isPartition() {
            return isPartition;
        }

        public void setPartition(boolean isPartition) {
            this.isPartition = isPartition;
        }

        public boolean isPrimaryKey() {
            return isPrimaryKey;
        }

        public void setPrimaryKey(boolean isPrimaryKey) {
            this.isPrimaryKey = isPrimaryKey;
        }


    }
}
