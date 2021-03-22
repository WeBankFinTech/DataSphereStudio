/*
 *
 *  * Copyright 2019 WeBank
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.webank.wedatasphere.dss.appconn.visualis.utils;

public class NumberUtils {

    public static Integer getInt(Object original){
        if(original instanceof Double){
            return ((Double) original).intValue();
        }
        return (Integer) original;
    }

    public static String parseDoubleString(String doubleString) {
        Double doubleValue = Double.parseDouble(doubleString);
        Integer intValue = doubleValue.intValue();
        return intValue.toString();
    }

}
