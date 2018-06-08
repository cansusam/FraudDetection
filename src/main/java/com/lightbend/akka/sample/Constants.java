package com.lightbend.akka.sample;

public class Constants {

    /**
     *
     *
     * schedulingDurationsInterval: [0,X] milliseconds used for scheduling of card requests
     */

    public static final int atmLimit = 81; // total ATM number 80 Local + 1 International
    public static final int terminalNumber = 4;
    public static final int cardNumber = 10;
    public static final int amountList[] = {100,1000,10000};
    public static final int numberOfTransactions = 100;
    public static final int schedulingDurationsMS[] = {1,10,100,1000,10000};
    public static final int schedulingDurationsInterval = 1000;
    public static final int timeAccelerator = 1000000;
}
