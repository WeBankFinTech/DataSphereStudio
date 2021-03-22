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

package com.webank.wedatasphere.dss.apiservice.core.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author: jinyangrao
 */
public class SQLCheckUtil {

    // sql 注入检查
    private static String injectionReg = "(?:--)|" +
                        "(\\b(select|update|union|and|or|delete|insert|trancate|char|substr|ascii|declare|exec|count|master|into|drop|execute)\\b)";
    private static Pattern paramInjectionPattern = Pattern.compile(injectionReg, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

    /**
     * 1. -- 注释
     * 2. select * from table -- 注释
     * 但是不包含 -- '注释'
     * */
    private static String sqlCommentReg = "\\-\\-([^\\'\\r\\n]{0,}(\\'[^\\'\\r\\n]{0,}\\'){0,1}[^\\'\\r\\n]{0,}){0,}";
    private static Pattern sqlCommentPattern = Pattern.compile(sqlCommentReg, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

    public static boolean doParamInjectionCheck(String str) {
        Matcher matcher = paramInjectionPattern.matcher(str);
        if(matcher.find()) {
            return true;
        }
        return false;
    }

    public static String sqlCommentReplace(String sql) {
        String newSql = sql.replaceAll(sqlCommentReg, "");
        return newSql;
    }

    public static void main(String[] args) {
        String sql = "--注释\n" +
                     "-- 注释\n" +
                     "select * from tb --注释\n";
        String newSql = sqlCommentReplace(sql);
        String[] selects = newSql.split("select");
        System.out.println(newSql);
        System.out.println(selects.length);
    }

}