package com.lightbend.akka.sample;

import static com.lightbend.akka.sample.Constants.atmLimit;

public class CardBalanceListElement {
    /**
     * Balance list used to check last situation of each card.
     * Balance list details
     * 1- CardID
     * 2- Limit
     * 3- Initialization Date
     * 4- Balance (Equal to limit at initialization).
     * 5- LastUpdate Date
     * 6- Card Type
     */

    public Integer cardID;
    public Integer limit;
    public String initDate;
    public Integer balance;
    public String lastUpdate;
    public Kind.cardKind cardType;
    public Integer statementDate;
    public Integer homeLocation;

    CardBalanceListElement(Integer cardID, Integer limit, String initDate, Integer balance, Kind.cardKind cardType,
                           Integer statementDate, Integer homeLocation){
        this.cardID = cardID;
        this.limit = limit;
        this.initDate = initDate;
        this.balance = balance;
        this.lastUpdate = initDate;
        this.cardType = cardType;
        this.statementDate = statementDate;
        this.homeLocation = homeLocation;
    }

    public String homeLocationToString(){
        if(homeLocation == atmLimit-1)
            return "International";
        return homeLocation.toString();
    }

}
