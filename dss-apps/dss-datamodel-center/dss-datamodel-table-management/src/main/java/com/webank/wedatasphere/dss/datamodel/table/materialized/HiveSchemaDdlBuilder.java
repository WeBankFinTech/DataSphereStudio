package com.webank.wedatasphere.dss.datamodel.table.materialized;

import com.webank.wedatasphere.dss.datamodel.table.materialized.HiveSchema.Column;
import org.apache.commons.lang.StringUtils;

import java.util.List;


public class HiveSchemaDdlBuilder {
    public static String lineSeparator = System.getProperty("line.separator");

    private String fieldsTerminated = "\'\\t\'";

    private String linesTerminated = "\'\\n\'";

    private HiveSchema hiveSchema;

    private boolean isExternal = false;

    public HiveSchemaDdlBuilder() {
        hiveSchema = new HiveSchema();
    }


    public HiveSchemaDdlBuilder tableName(String tableName) {
        hiveSchema.setTableName(tableName);
        return this;
    }

    public HiveSchemaDdlBuilder storedType(String storedType) {
        hiveSchema.setStoredType(storedType);
        return this;
    }

    public HiveSchemaDdlBuilder location(String location) {
        hiveSchema.setLocation(location);
        return this;
    }

    public HiveSchemaDdlBuilder comment(String comment) {
        hiveSchema.setComment(comment);
        return this;
    }


    public HiveSchemaDdlBuilder withExternal() {
        hiveSchema.setExternal(true);
        return this;
    }

    public HiveSchemaDdlBuilder fieldsTerminated(String fieldsTerminated) {
        this.fieldsTerminated = fieldsTerminated;
        return this;
    }

    public HiveSchemaDdlBuilder linesTerminated(String linesTerminated) {
        this.linesTerminated = linesTerminated;
        return this;
    }

    public HiveSchemaDdlBuilder dataBase(String dataBase) {
        hiveSchema.setDataBase(dataBase);
        return this;
    }

    public HiveSchemaDdlBuilder addColumn(String name, String type, int length, int percision, boolean isPartition, boolean isPrimaryKey, String comment) {
        hiveSchema.addColumn(name, type, length, percision, isPartition, isPrimaryKey, comment);
        return this;
    }

    public void addColumn(String name, String type,  boolean isPartition, String comment) {
        addColumn(name,type,0,0,isPartition,false,comment);
    }

    public String createTableString() {
        StringBuilder builder = new StringBuilder();

        builder.append("CREATE ");

        if (hiveSchema.isExternal()) {
            builder.append("EXTERNAL ");
        }

//        builder.append("TABLE " + hiveSchema.getDataBase() + "." + hiveSchema.getTableName()
//                + lineSeparator);
        builder.append("TABLE IF NOT EXISTS ").append(hiveSchema.getTableName()).append(lineSeparator);


        builder.append("( ");
        boolean isFirstColumn = true;
        for (Column c : hiveSchema.getColumns()) {
            if (!c.isPartition()) {
                if (isFirstColumn) {
                    isFirstColumn = false;
                } else {
                    builder.append(",");
                }
                builder.append(lineSeparator);
                builder.append(" ").append(c.getName()).append(" ").append(c.getType());
                if (c.getComment() != null && !c.getComment().equals("")) {
                    builder.append(" ").append("COMMENT '").append(c.getComment()).append("'");
                }
            }
        }

        builder.append(") ").append(lineSeparator);

        if (hiveSchema.getComment() != null && !hiveSchema.getComment().equals("")) {
            builder.append("COMMENT '").append(hiveSchema.getComment()).append("'").append(lineSeparator);
        }

        List<Column> partitionColumns = hiveSchema.getPartitionColumns();
        if (partitionColumns.size() > 0) {
            builder.append("PARTITIONED BY (");

            isFirstColumn = true;
            for (Column c : partitionColumns) {
                if (isFirstColumn) {
                    isFirstColumn = false;
                } else {
                    builder.append(", ");
                }
                builder.append(c.getName()).append(" ").append(c.getType());
            }

            builder.append(")").append(lineSeparator);
        }

        if (hiveSchema.getStoredType() != null) {
            builder.append("STORED AS ").append(hiveSchema.getStoredType()).append(lineSeparator);
        }

        if (hiveSchema.isExternal()&&StringUtils.isNotBlank(hiveSchema.getLocation())) {
            builder.append("LOCATION ").append("'").append(hiveSchema.getLocation()).append("'");
        }


        builder.append(";");

        return builder.toString();
    }

    private static String convertColumnType(Column column) {
        String dbType = column.getType().toUpperCase();

        if (dbType.equals("STRING") || dbType.equals("VARCHAR2")
                || dbType.equals("CHAR") || dbType.equals("VARCHAR")
                || dbType.equals("CLOB")) {
            return "STRING";
        } else if (dbType.equals("NUMBER") || dbType.equals("DECIMAL")
                || dbType.equals("BYTEINT") || dbType.equals("SMALLINT")
                || dbType.equals("INTEGER") || dbType.equals("BIGINT")
                || dbType.equals("INT") || dbType.equals("FLOAT")) {
            if (column.getPercision() == 0) {
                if (column.getLength() > 18) {
                    return "STRING";
                } else if (column.getLength() > 9 || dbType.equals("BIGINT")) {
                    return "BIGINT";
                } else if (column.getLength() > 4 || dbType.equals("INTEGER")) {
                    return "INT";
                } else if (column.getLength() > 2 || dbType.equals("SMALLINT")) {
                    return "SMALLINT";
                } else {
                    return "TINYINT";
                }
            } else {
                if (column.getLength() > 17) {
                    return "STRING";
                } else {
                    return "DOUBLE";
                }
            }

        } else if (dbType.equals("OTHERDATE") || dbType.equals("DATE")
                || dbType.equals("DATETIME") || dbType.equals("TIMESTAMP")) {
            return "TIMESTAMP";
        } else {
            throw new RuntimeException("Currently doesn't support " + dbType + " for column " + column.getName());
        }
    }

}
