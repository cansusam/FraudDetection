package com.lightbend.akka.sample;

public class TransactionListElement {
    /**
     * Transaction list used to keep record of each card payment request.
     * Transaction list element details
     * 1- CardID
     * 2- TerminalID
     * 3- Amount
     * 4- Balance
     * 5- Remaining
     * 6- Validity (1:True, 0:False)
     * 7- Date
     */

    public Integer cardID;
    public Integer terminalID;
    public Integer amount;
    public Integer balance;
    public Integer remaining;
    public Integer validity;
    public String date;

//    public Integer merchantCategory;
//    public String cardType;
//    public String terminalType;


    public TransactionListElement(Integer cardID,
                                  Integer terminalID,
                                  Integer amount,
                                  Integer balance,
                                  Integer remaining,
                                  Integer validity,
                                  String  date
//            ,
//                                  Integer merchantCategory,
//                                  Kind.cardKind cardType,
//                                  Kind.terminalKind terminalType
    ){
        this.cardID = cardID;
        this.terminalID = terminalID;
        this.amount = amount;
        this.balance = balance;
        this.remaining = remaining;
        this.validity = validity;
        this.date = date;
//
//        this.merchantCategory = merchantCategory;
//        this.cardType = cardType.toString();
//        this.terminalType = terminalType.toString();
    }
}
