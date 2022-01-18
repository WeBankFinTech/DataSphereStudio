package com.webank.wedatasphere.dss.datamodel.center.common.constant;


import org.apache.commons.lang.StringUtils;

import java.util.Optional;

public enum TabelExternalType {
    INNER(0,"MANAGED_TABLE"),
    EXTERNAL(1,"EXTERNAL_TABLE");

    private Integer code;

    private String type;

    TabelExternalType(Integer code, String type) {
        this.code = code;
        this.type = type;
    }

    public Integer getCode() {
        return code;
    }

    public String getType() {
        return type;
    }

    public static Optional<TabelExternalType> getByType(String type){
        for (TabelExternalType e : values()){
            if (StringUtils.equals(type,e.getType())){
                return Optional.of(e);
            }
        }
        return Optional.empty();
    }
}
