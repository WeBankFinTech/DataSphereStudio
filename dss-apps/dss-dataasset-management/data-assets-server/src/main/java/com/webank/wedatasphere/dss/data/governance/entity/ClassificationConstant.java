package com.webank.wedatasphere.dss.data.governance.entity;


import com.webank.wedatasphere.dss.data.governance.conf.GovernanceConf;
import org.apache.commons.lang.StringUtils;

import java.util.Optional;

public enum ClassificationConstant {
    /**
     * 指标
     */
    INDICATOR(1, "indicator_", GovernanceConf.ATLAS_ROOT_INDICATOR.getValue(),"indicator"),
    /**
     * 度量
     */
    MEASURE(2, "measure_", GovernanceConf.ATLAS_ROOT_MEASURE.getValue(),"measure"),
    /**
     * 维度
     */
    DIMENSION(0, "dimension_", GovernanceConf.ATLAS_ROOT_DIMENSION.getValue(),"dimension"),
    /**
     * 主题
     */
    THEME(3, "theme_", GovernanceConf.ATLAS_ROOT_THEME.getValue(),"theme"),
    /**
     * 分层
     */
    LAYER(4, "layer_", GovernanceConf.ATLAS_ROOT_LAYER.getValue(),"layer");

    private int type;

    private String prefix;

    private String root;

    private String typeCode;

    ClassificationConstant(int type, String prefix, String root,String typeCode) {
        this.type = type;
        this.prefix = prefix;
        this.root = root;
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

    public static Optional<String> formatName(int type, String name){
        return Optional.of(getPrefix(type).get() + name);
    }

    public static Optional<String> formatName(String typeCode, String name){
        return Optional.of(getPrefix(typeCode).get() + name);
    }

    public static Optional<String> getRoot(int type) {
        Optional<ClassificationConstant> optional = getClassificationConstantByType(type);
        return optional.map(ClassificationConstant::getRoot);
    }

    public static Optional<String> getRoot(String typeCode) {
        Optional<ClassificationConstant> optional = getClassificationConstantByTypeCode(typeCode);
        return optional.map(ClassificationConstant::getRoot);
    }

    public static Optional<String> getPrefix(int type) {
        Optional<ClassificationConstant> optional = getClassificationConstantByType(type);
        return optional.map(ClassificationConstant::getPrefix);
    }

    public static Optional<String> getPrefix(String typeCode) {
        Optional<ClassificationConstant> optional = getClassificationConstantByTypeCode(typeCode);
        return optional.map(ClassificationConstant::getPrefix);
    }


    public int getType() {
        return type;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getRoot() {
        return root;
    }

    public String getTypeCode() {
        return typeCode;
    }
}
