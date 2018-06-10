package com.lightbend.akka.sample;
import static com.lightbend.akka.sample.Constants.*;

/**
 * Each card (user) has different ranges for its not fraudulent use.
 */
public class ValidDistributionRanges {

    public Integer[] amountUpperLimits = new Integer[cardNumber];
    public Integer[] schedulingUpperLimits = new Integer[cardNumber];
    public Integer[] terminalUpperLimits = new Integer[cardNumber];
    public Integer[] amountLowerLimits = new Integer[cardNumber];
    public Integer[] schedulingLowerLimits = new Integer[cardNumber];
    public Integer[] terminalLowerLimits = new Integer[cardNumber];

}
