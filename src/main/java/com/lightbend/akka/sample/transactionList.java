package com.lightbend.akka.sample;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class transactionList extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    static public Props props() {
        return Props.create(transactionList.class, () -> new transactionList());
    }

    /**
     * Transactions received by terminals are kept in the list.
     */
    static public class receivedTransaction {
        public final Integer amount;
        public final Integer cardID;
        public final Integer terminalID;
        public final String date;

        /**
         *
         * @param amount
         * @param cardID
         * @param terminalID
         * @param date
         */
        public receivedTransaction(Integer amount, Integer cardID, Integer terminalID, String date) {
            this.amount = amount;
            this.cardID = cardID;
            this.terminalID = terminalID;
            this.date = date;
        }
    }

    /*
        This map will keep cards balanceList.
     Instead list, wrapper class can be used
    */
    private final HashMap<Integer, List<Integer>> balanceList = new HashMap<Integer, List<Integer>>();

    private final List<List<Integer>> transactionList = new ArrayList<>();

    /**
     * Transactions received by terminals are kept in the list.
     */
    static public class receivedCardInitialization {
        public final Integer limit;
        public final Integer cardID;
        public final String date;


        public receivedCardInitialization(Integer limit, Integer cardID, String date) {
            this.limit = limit;
            this.cardID = cardID;
            this.date = date;
        }
    }

    // TODO
    static public class printBalanceList{

    }

    // TODO
    static public class printTransactionList{

    }

    // constructor
    public transactionList() {

    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(receivedTransaction.class, received -> {
                    // balance value kept in 4th element
                    List<Integer> cardValues = balanceList.get(received.cardID);
                    int balance = cardValues.get(3);
                    int validity = 1;
                    if (balance > received.amount) {
                        // valid
                        int newBalance = balance - received.amount;
                        // log.info("\n#Valid Transaction Received: Terminal " + received.terminalID + " - Amount : " + received.amount + " - Previous Balance : " + balance + " - Remaining : " + newBalance + " - CardID : " + received.cardID);
                        System.out.println("#Valid Transaction Received: Terminal " + received.terminalID + " - Amount : " + received.amount + " - Previous Balance : " + balance + " - Remaining : " + newBalance + " - CardID : " + received.cardID);
                        cardValues.set(3,newBalance);
                        balanceList.put(received.cardID,cardValues);
                    } else {
                        // not valid
                        validity = 0;
                        //log.info("\n#Non-Valid Transaction Received: Terminal " + received.terminalID + " - Amount : " + received.amount + " - Balance : " + balance + " - CardID : " + received.cardID);
                        System.out.println("#Non-Valid Transaction Received: Terminal " + received.terminalID + " - Amount : " + received.amount + " - Balance : " + balance + " - CardID : " + received.cardID);
                    }

                    /**
                     * Transaction list details
                     * 1- CardID
                     * 2- Amount
                     * 3- Date
                     * 4- TerminalID
                     * 5- Balance
                     * 6- Validity (1:True, 0:False)
                     */
                    List<Integer> transactionValues = new ArrayList<>();
                    transactionValues.add(received.cardID);
                    transactionValues.add(received.amount);
                    transactionValues.add(Integer.parseInt(received.date));
                    transactionValues.add(received.terminalID);
                    transactionValues.add(balance);
                    transactionValues.add(validity);

                    transactionList.add(transactionValues);

                })
                .match(receivedCardInitialization.class, received -> {
                    // TODO type of card, house location should be added
                    // Wrapper class would be better
                    /**
                     * Balance list details
                     * 1- CardID
                     * 2- Limit
                     * 3- Date in Integer format
                     * 4- Balance (Equal to limit at initialization).
                     */
                    List<Integer> cardValues = new ArrayList<>();
                    cardValues.add(received.cardID);
                    cardValues.add(received.limit);
                    cardValues.add(Integer.parseInt(received.date));
                    cardValues.add(received.limit);
                    balanceList.put(received.cardID,cardValues);
                    //log.info("\nCard information received from Card ID : " + received.cardID + " Card Limit: " + received.limit + " at " + received.date + " - Card initialized.");
                    System.out.println("Card information received from Card ID : " + received.cardID + " Card Limit: " + received.limit + " at " + received.date + " - Card initialized.");
                })
                .matchAny(o -> log.info("received unknown message"))
                .build();
    }
//#Card-messages
}
//#Card-messages
