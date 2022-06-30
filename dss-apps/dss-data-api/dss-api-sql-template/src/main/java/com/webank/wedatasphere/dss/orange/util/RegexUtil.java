package com.webank.wedatasphere.dss.orange.util;

import java.util.regex.Pattern;


public class RegexUtil {

    public static String replace(String content, String item, String newItem) {
        return content.replaceFirst("^\\s*" + item + "(?![^.,:\\s])", newItem);
    }

    public static void main(String[] args) {
        boolean matches = "item".matches( "item" + "[.,:\\s\\[]");

        boolean item = Pattern.compile("item[.,:\\s\\[]").matcher("item").matches();

//        String aa = "item[0].name".replaceFirst("^\\s*" + "item" + "(?![^.,:\\s])", "aa");
//        System.out.println(aa);
        System.out.println(item);
    }
}
