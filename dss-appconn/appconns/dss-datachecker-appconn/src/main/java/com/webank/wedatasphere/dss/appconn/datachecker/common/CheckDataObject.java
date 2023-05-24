package com.webank.wedatasphere.dss.appconn.datachecker.common;

import org.apache.commons.lang3.StringUtils;

/**
 * 检查的数据对象
 * Author: xlinliu
 * Date: 2023/5/11
 */
public class CheckDataObject {
    /**
     * 检查对象的类型
     */
    private Type type;
    private String dbName;
    private String tableName;
    /**
     * 分区明，只有当type等于PARTITION才有值
     */
    private String partitionName;

    public CheckDataObject(String dbName, String tableName, String partitionName) {
        this.dbName = dbName;
        this.tableName = tableName;
        this.partitionName = partitionName;
        type= StringUtils.isNotEmpty(partitionName)?Type.PARTITION:Type.TABLE;
    }

    public CheckDataObject(String dbName, String tableName) {
        this.dbName = dbName;
        this.tableName = tableName;
        type = Type.TABLE;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getPartitionName() {
        return partitionName;
    }

    public void setPartitionName(String partitionName) {
        this.partitionName = partitionName;
    }

    public enum Type{
        /**
         * 分区对象
         */
        PARTITION,
        /**
         * 表对象
         */
        TABLE,

    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append(dbName).append(tableName).append(partitionName);
        return sb.toString();
    }
}
