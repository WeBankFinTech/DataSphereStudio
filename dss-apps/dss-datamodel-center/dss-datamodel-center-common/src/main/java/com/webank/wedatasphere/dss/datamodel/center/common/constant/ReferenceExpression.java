package com.webank.wedatasphere.dss.datamodel.center.common.constant;


public enum ReferenceExpression {

    EXPRESSION;

    private final static String oriCode ="(\"%s\":)(\"|\".*,)(%s)(\"|,)";

    private final static String regexpCode = " REGEXP '"+oriCode+"' ";

    private String getFormatExpCode(String key, String value){
        return String.format(oriCode,key,value);
    }

    public String getSqlExpCode(String key,String value){
        return String.format(regexpCode,key,value);
    }


    public static void main(String[] args) {
        System.out.println(ReferenceExpression.EXPRESSION.getSqlExpCode("warehouseLayerNameEn","year"));
    }
}

