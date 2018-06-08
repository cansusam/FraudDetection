package com.lightbend.akka.sample;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeConverter {

    public static long startTime;
    private static int timeAccelerator = Constants.timeAccelerator;

    public static void timeToDate(long milliseconds){
//        long yourmilliseconds = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date resultdate = new Date(milliseconds);
//        System.out.println(sdf.format(resultdate));
    }

    public static String returnTime(long currentTimeOfRequest) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        long passedTime  = currentTimeOfRequest-startTime;
        long artificialCurrentTime = passedTime*timeAccelerator + startTime;
        Date resultdate = new Date(artificialCurrentTime);
        return sdf.format(resultdate);
    }


}
