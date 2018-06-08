package com.lightbend.akka.sample;

public class BalanceListElement {
    /**
     * Balance list used to check last situation of each card.
     * Balance list details
     * 1- CardID
     * 2- Limit
     * 3- Date in Integer format
     * 4- Balance (Equal to limit at initialization).
     */

    public Integer cardID;
    public Integer limit;
    public String date;
    public Integer balance;

    BalanceListElement(Integer cardID, Integer limit, String date, Integer balance){
        this.cardID = cardID;
        this.limit = limit;
        this.date = date;
        this.balance = balance;
    }

}
