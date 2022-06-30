package com.webank.wedatasphere.dss.data.api.server.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

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

    public static ArrayList<String> getTimeLagForPast24H() throws Exception {
        //创建集合存储所有时间点
        ArrayList<String> list = new ArrayList<String> ();

        String format = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(format);

        Calendar ca = Calendar.getInstance();
        ca.set(Calendar.MINUTE, 0);
        ca.set(Calendar.SECOND, 0);

        SimpleDateFormat sdfHour = new SimpleDateFormat("yyyy-MM-dd HH:00");
        for(int i= 0; i< 24; i++){
            list.add(sdfHour.format(ca.getTime()));
            ca.add(Calendar.HOUR,-1);
        }

        Collections.reverse(list);
        return list;
    }

    //获取时间段内小时整点
    public static ArrayList<String> getTimeLag(String startTime, String endTime) throws Exception {
        //创建集合存储所有时间点
        ArrayList<String> list = new ArrayList<String> ();

        String format = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date dBegin = sdf.parse(startTime);
        Date dEnd = sdf.parse(endTime);

        Calendar ca = Calendar.getInstance();
        ca.setTime(dEnd);
        ca.set(Calendar.MINUTE, 0);
        ca.set(Calendar.SECOND, 0);
        Date dEndHour = ca.getTime();

        SimpleDateFormat sdfHour = new SimpleDateFormat("yyyy-MM-dd HH:00");
        while(dEndHour.compareTo(dBegin) >=0){
            list.add(sdfHour.format(dEndHour));

            ca.add(Calendar.HOUR,-1);
            dEndHour =ca.getTime();
        }

        Collections.reverse(list);
        return list;
    }

    /**
     * 获取当前时间的整点小时时间
     */
    public static String getCurrHourTime( ){
        Calendar ca = Calendar.getInstance();
        ca.set(Calendar.MINUTE, 0);
        ca.set(Calendar.SECOND, 0);
        Date date = ca.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:00");
        return sdf.format(date);
    }

    public static void main(String[] args) throws Exception {
        //System.out.println(getHourCnt("2021-07-29 00:00:00","2021-08-05 00:00:00"));
        System.out.println(getTimeLag("2021-08-11 00:10:00","2021-08-12 00:20:00"));
    }
}
