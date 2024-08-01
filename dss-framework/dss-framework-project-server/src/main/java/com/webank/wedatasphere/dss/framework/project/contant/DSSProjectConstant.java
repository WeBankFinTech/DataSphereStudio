package com.webank.wedatasphere.dss.framework.project.contant;

import org.apache.commons.lang3.StringUtils;

public class DSSProjectConstant {

    // 升序
    public static final String ASCEND = "ascend";
    // 降序
    public static final String DESCEND = "descend";


    public static String concatOrderBySql(String sortBy, String orderBy) {

        if (StringUtils.isEmpty(sortBy) || StringUtils.isEmpty(orderBy)) {
            return null;
        }

        String[] sortArray = sortBy.split(",");
        String[] orderArray = orderBy.split(",");
        if (sortArray.length != orderArray.length) {
            return null;
        }
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < sortArray.length; i++) {
            buffer.append(sortArray[i]);
            if (DESCEND.equalsIgnoreCase(orderArray[i])) {
                buffer.append(" asc");
            } else {
                buffer.append(" desc");
            }

            if (i < sortArray.length - 1) {
                buffer.append(",");
            }
        }

        return buffer.toString();

    }

}
