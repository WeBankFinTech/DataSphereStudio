package com.webank.wedatasphere.dss.framework.dbapi.util;

import java.text.SimpleDateFormat;

/**
 * @Classname TimeUtil
 * @Description TODO
 * @Date 2021/8/10 14:35
 * @Created by suyc
 */
public class TimeUtil {
    /**
     * 时间段的小时数
     */
    public static long getHourCnt(String startTime,String endTime) throws Exception {
        return dateDiff(startTime,endTime,"yyyy-MM-dd HH:mm:ss");
    }

    private static long dateDiff(String startTime, String endTime, String format) throws Exception {
        SimpleDateFormat sd = new SimpleDateFormat(format);
        //long nd = 1000*24*60*60; //一天的毫秒数
        long nh = 1000*60*60; //一小时的毫秒数
        //long nm = 1000*60; //一分钟的毫秒数
        //long ns = 1000; //一秒钟的毫秒数
        long diff;
        //获得两个时间的毫秒时间差异
        diff = sd.parse(endTime).getTime() - sd.parse(startTime).getTime();
        //long day = diff/nd;//计算差多少天
        //long hour = diff % nd / nh;//计算差多少小时
        //long min = diff%nd%nh/nm;//计算差多少分钟
        //long sec = diff%nd%nh%nm/ns;//计算差多少秒//输出结果
        //System.out.println("时间相差："+day+"天"+hour+"小时"+min+"分钟"+sec+"秒。");

        long hour = diff / nh;//计算差多少小时
        return hour ;
    }

    public static void main(String[] args) throws Exception {
        System.out.println(getHourCnt("2021-07-29 00:00:00","2021-08-05 00:00:00"));
    }
}
