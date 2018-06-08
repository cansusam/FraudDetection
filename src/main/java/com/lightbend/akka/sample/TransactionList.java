package com.lightbend.akka.sample;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.lightbend.akka.sample.Writer.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TransactionList extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    static public Props props() {
        return Props.create(TransactionList.class, () -> new TransactionList());
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

    /**
     * balanceList keeps cards balance list.
     * Wrapper can be used instead of List.
     */
    private final HashMap<Integer, List<Integer>> balanceList = new HashMap<Integer, List<Integer>>();
    /**
     * TransactionList keeps all transaction logs weather it is valid or not.
     */
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

    // It is not necessary
//    static public class printBalanceList{
//
//    }

    // constructor
    public TransactionList() {

    }

    /**
     * Creates its own writerTransactions for transactions
     */
    final ActorRef writerTransactions = getContext().system().actorOf(Writer.props());
    @Override
    public void preStart() throws IOException {
        writerTransactions.tell(new transactionFileCreate(),getSelf());
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(receivedTransaction.class, received -> {
                    // balance value kept in 4th element
                    List<Integer> cardValues = balanceList.get(received.cardID);
                    int balance = cardValues.get(3);
                    int validity = 1;
                    int newBalance = balance;
                    if (balance >= received.amount) {
                        // valid
                        newBalance = balance - received.amount;
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

                    // TODO do it with wrapper class!
                    /**
                     * Transaction list details
                     * 1- CardID
                     * 2- TerminalID
                     * 3- Amount
                     * 4- Balance
                     * 5- Remaining
                     * 6- Validity (1:True, 0:False)
                     * 7- Date
                     */
                    List<Integer> transactionValues = new ArrayList<>();
                    transactionValues.add(received.cardID);
                    transactionValues.add(received.terminalID);
                    transactionValues.add(received.amount);
                    transactionValues.add(balance);
                    transactionValues.add(newBalance);
                    transactionValues.add(validity);
                    transactionValues.add(Integer.parseInt(received.date));

                    transactionList.add(transactionValues);
                    writerTransactions.tell(new transactionAddLine(transactionValues),getSelf());

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
