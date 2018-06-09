package com.lightbend.akka.sample;
import static com.lightbend.akka.sample.Constants.*;
public class ValidDistributionRanges {

    public Integer[] amountUpperLimits = new Integer[amountList.length];
    public Integer[] schedulingUpperLimits = new Integer[schedulingDurationsMS.length];
    public Integer[] terminalUpperLimits = new Integer[terminalNumber];
    public Integer[] amountLowerLimits = new Integer[amountList.length];
    public Integer[] schedulingLowerLimits = new Integer[terminalNumber];
    public Integer[] terminalLowerLimits = new Integer[terminalNumber];

}
