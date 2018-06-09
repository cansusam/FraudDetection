package com.lightbend.akka.sample;


/**
 * All constants used in simulator stored in this class.
 */
public class Constants {

    //
    public static final int atmLimit = 81; // total ATM number 80 Local + 1 International
    public static final int merchantLimit = 5;
    public static final int terminalNumber = 200;
    public static final int cardNumber = 100;
    public static final int amountList[] = {100,1000,10000};

    // Time and Scheduling
    public static final int schedulingDurationsMS[] = {1,10,100,1000,10000};
    /**
     * [0,X] milliseconds used for scheduling of card requests
     */
    public static final int schedulingDurationsInterval = 10000;
    public static final boolean schedulingWithRandomIntervalON = true;
    /**
     * speed up time X times
     */
    public static final int timeAccelerator = 100000;
    public static final String datePattern = "yyyy-MM-dd HH:mm:ss.SSS";

    // Actor parameters
    /**
     * credit card limits
     */
    public static final Integer[] cardLimitPool = {5000,10000,20000,30000};
    /**
     * credit card limit occurrence ratio (ordered)
     */
    public static final Integer[] limitRatios = {4,3,2,1};
    /**
     * Debit card limit
     */
    public static final Integer debitLimit = 1000;
    /**
     * TODO this is used to prevent day/month incompatibility
     * e.g. if month is february and statement date is higher than 28/29
     */
    public static final int maxValueOfStatementDay = 28;

    // Simulation mode constants
    public static boolean endlessSimulationON = false;
    public static final int numberOfTransactions = 100;
}
