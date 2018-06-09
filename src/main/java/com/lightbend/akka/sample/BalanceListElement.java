package com.lightbend.akka.sample;

public class BalanceListElement {
    /**
     * Balance list used to check last situation of each card.
     * Balance list details
     * 1- CardID
     * 2- Limit
     * 3- Initialization Date
     * 4- Balance (Equal to limit at initialization).
     * 5- LastUpdate Date
     */

    public Integer cardID;
    public Integer limit;
    public String initDate;
    public Integer balance;
    public String lastUpdate;

    BalanceListElement(Integer cardID, Integer limit, String initDate, Integer balance){
        this.cardID = cardID;
        this.limit = limit;
        this.initDate = initDate;
        this.balance = balance;
        this.lastUpdate = initDate;
    }

}
