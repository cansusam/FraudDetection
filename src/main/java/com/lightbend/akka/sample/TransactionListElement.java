package com.lightbend.akka.sample;

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
public class TransactionListElement {

    public Integer cardID;
    public Integer terminalID;
    public Integer amount;
    public Integer balance;
    public Integer remaining;
    public Integer validity;
    public String date;

    public TransactionListElement(Integer cardID, Integer terminalID, Integer amount, Integer balance,
                                  Integer remaining, Integer validity, String  date){
        this.cardID = cardID;
        this.terminalID = terminalID;
        this.amount = amount;
        this.balance = balance;
        this.remaining = remaining;
        this.validity = validity;
        this.date = date;
    }
}
