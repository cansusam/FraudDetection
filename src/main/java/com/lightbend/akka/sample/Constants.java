package com.lightbend.akka.sample;


/**
 * All constants used in simulator stored in this class.
 */
public class Constants {

    // Simulation
    public static final int atmLimit = 81; // total ATM number 80 Local + 1 International
    public static final int merchantLimit = 5;
    public static final int terminalNumber = 1;
    public static final int cardNumber = terminalNumber*10;
    public static final int amountList[] = {100,500,1000,2000,3000,4000,5000,7000,10000,12000};

    // Simulation mode constants
    public static boolean endlessSimulationON = false;
    public static final int numberOfTransactions = 100;

    // Time and Scheduling
    public static final int schedulingDurationsMS[] = {10,100,500,1000,2000,3000,4000,7000,8000,10000};
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

    /**
     * Gaussian distributed values for scheduling and terminal selection
     */
    public static final boolean gaussDistroON = false;
    public static final boolean multiVariateON = true;
    public static final int distributedFeatureNumber = 3;
    public static final double windowRatio = 0.4;



}
