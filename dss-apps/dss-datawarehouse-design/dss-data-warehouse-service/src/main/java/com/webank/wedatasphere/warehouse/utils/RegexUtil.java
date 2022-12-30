package com.webank.wedatasphere.warehouse.utils;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtil {

    private static final String REGEX_EN_NAME = "^\\w+$";
    private static final String REGEX_CN_NAME = "^[\\u4E00-\\u9FA50-9_]+$";
    private static final Pattern enNamePattern = Pattern.compile(REGEX_EN_NAME);
    private static final Pattern cnNamePattern = Pattern.compile(REGEX_CN_NAME);

    public static boolean checkEnName(String source) {
        if (Objects.isNull(source)) {
            return false;
        }

        return enNamePattern.matcher(source).find();
    }

    public static boolean checkCnName(String source) {
        if (Objects.isNull(source)) {
            return false;
        }

        return cnNamePattern.matcher(source).find();
    }

}
