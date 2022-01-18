package com.webank.wedatasphere.dss.datamodel.center.common.constant;


import com.webank.wedatasphere.dss.data.governance.entity.ClassificationConstant;

public enum ModeType {

    /**
     * 指标
     */
    INDICATOR(ClassificationConstant.INDICATOR.getType(), ClassificationConstant.INDICATOR.getTypeCode()),
    /**
     * 度量
     */
    MEASURE(ClassificationConstant.MEASURE.getType(), ClassificationConstant.MEASURE.getTypeCode()),
    /**
     * 维度
     */
    DIMENSION(ClassificationConstant.INDICATOR.getType(), ClassificationConstant.INDICATOR.getTypeCode());


    private int type;



    private String typeCode;

    ModeType(int type, String typeCode) {
        this.type = type;

        this.typeCode = typeCode;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }
}
