package com.webank.wedatasphere.dss.framework.admin.common.domain;


public class PositionBo {
    /**
     * 行号
     */
    private int row;

    /**
     * 列号
     */
    private int column;

    public PositionBo(Integer row, Integer column) {
        this.row = row;
        this.column = column;
    }

    public PositionBo() {
    }

    @Override
    public String toString() {
        return "PositionBo{" +
                "row=" + row +
                ", column=" + column +
                '}';
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }
}