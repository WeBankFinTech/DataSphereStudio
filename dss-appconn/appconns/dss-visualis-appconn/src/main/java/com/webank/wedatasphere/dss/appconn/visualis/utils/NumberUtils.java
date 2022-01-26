/*
 * Copyright 2019 WeBank
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.webank.wedatasphere.dss.appconn.visualis.utils;

import org.apache.commons.lang.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NumberUtils {

    public static Integer getInt(Object original) {
        if (original instanceof Double) {
            return ((Double) original).intValue();
        }
        return (Integer) original;
    }

    public static String parseDoubleString(String doubleString) {
        if (isDouble(doubleString)) {
            Double doubleValue = Double.parseDouble(doubleString);
            Integer intValue = doubleValue.intValue();
            return intValue.toString();
        }
        return doubleString;

    }

    /**
     * 判断字符串是不是double型
     *
     * @param str
     * @return
     */
    public static boolean isDouble(String str) {
        if (StringUtils.isEmpty(str)) {
            return false;
        }
        Pattern pattern = Pattern.compile("[0-9]+[.]?[0-9]*[dD]?");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }


}
