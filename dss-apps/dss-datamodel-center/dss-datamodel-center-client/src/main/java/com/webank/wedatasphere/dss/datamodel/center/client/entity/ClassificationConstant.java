package com.webank.wedatasphere.dss.datamodel.center.client.entity;


import org.apache.commons.lang.StringUtils;

import java.util.Optional;

public enum ClassificationConstant {
    /**
     * 指标
     */
    INDICATOR(1, "indicator"),
    /**
     * 度量
     */
    MEASURE(2,"measure"),
    /**
     * 维度
     */
    DIMENSION(0, "dimension"),
    /**
     * 主题
     */
    THEME(3, "theme"),
    /**
     * 分层
     */
    LAYER(4, "layer");

    private int type;


    private String typeCode;

    ClassificationConstant(int type, String typeCode) {
        this.type = type;

        this.typeCode = typeCode;
    }

    public static boolean isTypeScope(int type) {
        return type >= 0 && type < values().length;
    }

    public static boolean isTypeScope(String type) {
        return getClassificationConstantByTypeCode(type).isPresent();
    }

    public static Optional<ClassificationConstant> getClassificationConstantByTypeCode(String typeCode) {
        for (ClassificationConstant c : values()) {
            if (StringUtils.equals(c.typeCode,typeCode)) {
                return Optional.of(c);
            }
        }
        return Optional.empty();
    }

    public static Optional<ClassificationConstant> getClassificationConstantByType(int type) {
        for (ClassificationConstant c : values()) {
            if (c.type == type) {
                return Optional.of(c);
            }
        }
        return Optional.empty();
    }



    public int getType() {
        return type;
    }

    public String getTypeCode() {
        return typeCode;
    }
}
