package com.webank.wedatasphere.dss.framework.dbapi.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommUtil {
    public static String[] objectToArray(Object object){
        String[] newArray = null;
        List<String> result = new ArrayList<>();
        if (object instanceof ArrayList<?>) {
            for (Object o : (List<?>) object) {
                result.add(String.valueOf(o));
            }
        }

        newArray = new String[result.size()];
        for (int i = 0; i < result.size(); i++) {
            newArray[i] = result.get(i);
        }
        return  newArray;
    }
}
