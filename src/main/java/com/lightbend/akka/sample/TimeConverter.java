package com.lightbend.akka.sample;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.lightbend.akka.sample.Constants.*;

/**
 * Simulation time is accelerated by multiplying current time with "timeaccelerator" constant.
 */
public class TimeConverter {

    public static long startTime;
    private static int timeAccelerator = Constants.timeAccelerator;

    public static String returnTime(long currentTimeOfRequest) {
        SimpleDateFormat sdf = new SimpleDateFormat(datePattern);
        long passedTime  = currentTimeOfRequest-startTime;
        long artificialCurrentTime = passedTime*timeAccelerator + startTime;
        Date resultdate = new Date(artificialCurrentTime);
        return sdf.format(resultdate);
    }

}
